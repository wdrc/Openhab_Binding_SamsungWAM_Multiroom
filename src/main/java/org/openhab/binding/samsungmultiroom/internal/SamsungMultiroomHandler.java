/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.samsungmultiroom.internal;

import static org.openhab.binding.samsungmultiroom.internal.SamsungMultiroomBindingConstants.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.NextPreviousType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * The {@link SamsungMultiroomHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author WDRC - Initial contribution
 */
@NonNullByDefault
public class SamsungMultiroomHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SamsungMultiroomHandler.class);
    private HttpClient httpClient;
    private @Nullable SamsungMultiroomConfiguration config;
    private boolean enableBackgroundListener = true;

    public SamsungMultiroomHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void dispose() {
        enableBackgroundListener = false;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.trace("Handle command {} {} ", channelUID, command);

        // handle refresh commands for ANY channel:
        if (command.getClass().equals(org.openhab.core.types.RefreshType.class)) {
            queryDeviceStatus();
        }
        // new method of receiving RefreshTypes
        if (command instanceof RefreshType) {
            queryDeviceStatus();
        }

        switch (channelUID.getId().toLowerCase()) {
            case CHANNEL_MUTE:
                setMute(command, channelUID);
                break;
            case CHANNEL_CONTROL:
                if (command instanceof PlayPauseType) {
                    if (command == PlayPauseType.PLAY) {
                        setPlayControl("play");
                    } else if (command == PlayPauseType.PAUSE) {
                        setPlayControl("pause");
                    }
                    // setPlayControl("resume");
                    //
                }
                if (command instanceof NextPreviousType) {
                    if (command == NextPreviousType.NEXT) {
                        // getCoordinatorHandler().next();

                    } else if (command == NextPreviousType.PREVIOUS) {
                        // getCoordinatorHandler().previous();
                    }
                }
                break;
            case CHANNEL_VOLUME:
                if (command instanceof PercentType) {
                    setVolume((PercentType) command);
                }
                break;
            case CHANNEL_PLAYURI:
                if (command instanceof StringType) {
                    setPlayUrl((StringType) command);
                }
                break;
            case CHANNEL_PLAYPRESET:
                if (command instanceof DecimalType) {
                    setPlayPreset((DecimalType) command);
                }
                break;
            default: {
                logger.info("Unknown command {} {} {} ", channelUID, command, command.getClass().toString());
                break;
            }
        }
    }

    private void setPlayPreset(DecimalType command) {
        String URL = HTTPCOMMAND_PLAYPRESETURL.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname);
        URL = URL.replaceAll(PLACEHOLDER_FOR_VALUE, String.valueOf(command.intValue()));
        sendUICRequest(URL);
    }

    private void setPlayUrl(StringType command) {
        String URL = HTTPCOMMAND_PLAYURL.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname);
        URL = URL.replaceAll(PLACEHOLDER_FOR_VALUE, command.toString());
        sendUICRequest(URL);
    }

    private void setPlayControl(String action) {
        // TODO Auto-generated method stub
        String URL = HTTPCOMMAND_SETPLAYCONTROL.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname);
        URL = URL.replaceAll(PLACEHOLDER_FOR_VALUE, action);
        sendUICRequest(URL);

        // bug: reply on "resume" command is not answered with a confirmation but with "<playtime>". Fix by
        queryDeviceStatus();
    }

    private void setVolume(PercentType command) {
        String URL = HTTPCOMMAND_SETVOLUME.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname);
        URL = URL.replaceAll(PLACEHOLDER_FOR_VALUE, String.valueOf(command.intValue()).toString());
        sendUICRequest(URL);
    }

    private void setMute(Command command, ChannelUID channelUID) {
        if (command instanceof OnOffType || command instanceof OpenClosedType || command instanceof UpDownType) {
            if (command.equals(OnOffType.ON) || command.equals(UpDownType.UP) || command.equals(OpenClosedType.OPEN)) {
                // set mute
                sendUICRequest(HTTPCOMMAND_MUTEON.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            } else {
                // disable mute,
                sendUICRequest(HTTPCOMMAND_MUTEOFF.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            }
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(SamsungMultiroomConfiguration.class);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            queryDeviceStatus();

            triggerStartOfBackgroundListener();

        });

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    public void triggerStartOfBackgroundListener() {
        // background task to have an open HTTP connection to the speaker to catch changes to the speaker originating
        // from another source then openhab
        // must be start in Background process
        // Function will loop until enableBackgroundListeren is set to false

        if (enableBackgroundListener) {
            sendUICRequestASync(HTTPCOMMAND_GETOPENLISTENER.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname)); // 0=no
            // timeout
        }
    }

    public void queryDeviceStatus() {

        if (config.hostname == null) {
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.NONE, "No Hostname configured.");
        } else {
            // Step: send GetName command to get name and detect connectivity
            sendUICRequest(HTTPCOMMAND_GETNAME.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            sendUICRequest(HTTPCOMMAND_GETMUTE.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            sendUICRequest(HTTPCOMMAND_GETVOLUME.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            sendUICRequest(HTTPCOMMAND_GETGROUPNAME.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            sendUICRequest(HTTPCOMMAND_GETRADIOINFO.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
            sendUICRequest(HTTPCOMMAND_GETMUSICSTATUS.replaceAll(PLACEHOLDER_FOR_HOSTNAME, config.hostname));
        }
    }

    private @Nullable UIC sendUICRequest(String URL) {
        return sendUICRequestSync(URL, 2000);
        // sendUICRequestSync(URL, 2000);
        // return null;
    }

    private @Nullable UIC sendUICRequestSync(String URL, long timeout) {
        try {
            logger.trace("HTTP Client Load {}", URL);
            httpClient.setConnectTimeout(timeout);
            // SYNCED METHOD
            ContentResponse cr = httpClient.newRequest(URL).method(HttpMethod.GET).send();

            String contentAsString = cr.getContentAsString();
            logger.trace("HTTP Client Result {} Size {} message {} ", cr.getStatus(), contentAsString.length(),
                    contentAsString);

            XStream xstream = new XStream(new StaxDriver());
            xstream.ignoreUnknownElements();
            xstream.allowTypesByWildcard(new String[] { "org.openhab.binding.samsungmultiroom.internal.**" });
            xstream.setClassLoader(this.getClass().getClassLoader()); // <-- very important to prevent
                                                                      // CannotResolveClassException

            xstream.processAnnotations(Response.class);
            xstream.processAnnotations(UIC.class);
            xstream.alias("UIC", UIC.class);
            xstream.alias("CPM", UIC.class);
            xstream.alias("response", Response.class);

            UIC responds = (UIC) xstream.fromXML(contentAsString);

            processUICResponds(responds);

            return responds;
        } catch (TimeoutException ex) {
            logger.trace("HTTP Client Timeout Exception {}", ex.getMessage());
            return null;
        } catch (Exception ex) {
            logger.trace("HTTP Client Exception {} {} {}", ex.getClass().toString(), ex.getMessage(),
                    ex.getStackTrace());
            return null;
        }
    }

    private void sendUICRequestASync(String URL) {

        logger.trace("HTTP Client Load {}", URL);
        // ASYNCED METHOD

        httpClient.newRequest(URL).method(HttpMethod.GET).send(new BufferingResponseListener(8 * 1024 * 1024) {

            @Override
            public void onComplete(@Nullable Result result) {
                if (!result.isFailed()) {
                    String contentAsString = getContentAsString();
                    logger.trace("HTTP Client Result {} Size {} message {} ", result.getResponse().getStatus(),
                            contentAsString.length(), contentAsString);

                    XStream xstream = new XStream(new StaxDriver());
                    xstream.ignoreUnknownElements();
                    xstream.allowTypesByWildcard(new String[] { "org.openhab.binding.samsungmultiroom.internal.**" });
                    xstream.setClassLoader(this.getClass().getClassLoader()); // <-- very important to prevent
                                                                              // CannotResolveClassException

                    xstream.processAnnotations(Response.class);
                    xstream.processAnnotations(UIC.class);
                    xstream.alias("UIC", UIC.class);
                    xstream.alias("CPM", UIC.class);
                    xstream.alias("response", Response.class);

                    UIC responds = (UIC) xstream.fromXML(contentAsString);

                    processUICResponds(responds);
                    triggerStartOfBackgroundListener();
                } else {
                    // failed request
                    logger.trace("HTTP Client Exception {} {} ", result.getResponseFailure().toString(),
                            result.getRequestFailure().getMessage());

                }

            }
        });

    }

    private void processUICResponds(UIC res) {
        if (res != null) {
            // responds received, so device is online
            updateStatus(ThingStatus.ONLINE);

            String errCode = res.getFirstResponse().getErrCode();
            if (errCode != null) {
                logger.error("Received ERROR from speaker {} Command {} ErrCode {} ", this.toString(), res.getMethod(),
                        errCode);
            }

            // update name channel if mentioned
            String speakerName = res.getFirstResponse().getSpkname();
            if (speakerName != null) {
                logger.info("Name set to {} ", speakerName);
                updateState(CHANNEL_NAME, new StringType(speakerName));
            }

            String muteState = res.getFirstResponse().getMute();
            if (muteState != null) {
                logger.trace("Speaker mute state is {} ", muteState);

                if (muteState.equals("on")) {
                    logger.info("Speaker was muted");
                    updateState(CHANNEL_MUTE, OnOffType.from(true));

                } else {
                    logger.info("Speaker was unmuted");
                    updateState(CHANNEL_MUTE, OnOffType.from(false));

                }
            }

            String pwrOn = res.getFirstResponse().getPwron();
            if (pwrOn != null) {
                updateState(CHANNEL_STATE, new StringType(pwrOn));
            }

            String volume = res.getFirstResponse().getVolume();
            if (volume != null) {
                logger.info("{} Volume is {} ", super.thing.getLabel(), volume);
                updateState(CHANNEL_VOLUME, new PercentType(volume));
            }

            String groupName = res.getFirstResponse().getGroupname();
            if (groupName != null) {
                logger.info("Groupname is {} ", groupName);
                updateState(CHANNEL_ZONENAME, new StringType(groupName));
                updateState(CHANNEL_ZONEGROUPID, new StringType(groupName));

            }

            String playstatus = res.getFirstResponse().getPlaystatus();
            if (playstatus != null) {
                logger.info("PlayStatus is {} ", playstatus);
                if (playstatus.toLowerCase().equals("play")) {
                    updateState(CHANNEL_CONTROL, PlayPauseType.PLAY);
                } else if (playstatus.toLowerCase().equals("pause")) {
                    updateState(CHANNEL_CONTROL, PlayPauseType.PAUSE);
                } else if (playstatus.toLowerCase().equals("stop")) {
                    updateState(CHANNEL_CONTROL, PlayPauseType.PAUSE);
                } else {
                    logger.warn("PlayStatus received but could map to OpenHab Control. Unknown value is {} ",
                            playstatus);
                }
            }

            String title = res.getFirstResponse().getTitle();
            if (title != null) {
                logger.info("Title is {} ", title);
                updateState(CHANNEL_CURRENTTITLE, new StringType(title));
            }
            String artist = res.getFirstResponse().getArtist();
            if (artist != null) {
                logger.info("Artist is {} ", artist);
                updateState(CHANNEL_CURRENTARTIST, new StringType(artist));
            }
            String album = res.getFirstResponse().getAlbum();
            if (album != null) {
                logger.info("Album is {} ", album);
                updateState(CHANNEL_CURRENTALBUM, new StringType(album));
            }
            String albumArtUrl = res.getFirstResponse().getThumbnail();
            if (albumArtUrl != null) {
                logger.info("AlbumArtURL is {} ", albumArtUrl);
                updateState(CHANNEL_CURRENTALBUMARTURL, new StringType(albumArtUrl));

                scheduler.submit(() -> {
                    ContentResponse cr;
                    try {
                        cr = httpClient.newRequest(albumArtUrl).method(HttpMethod.GET).send();
                        RawType image = new RawType(cr.getContent(), "image/jpeg");
                        updateState(CHANNEL_CURRENTALBUMART, image);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });

            }

            String subMode = res.getFirstResponse().getSubmode();
            String function = res.getFirstResponse().getFunction();
            if (function != null) {
                logger.info("Function is {} submode is {} ", function, subMode);
                updateState(CHANNEL_INPUTS, new StringType(function));
            }

            String cpname = res.getFirstResponse().getCpname();
            if (cpname != null) {
                logger.info("cpname/service is {}", cpname);
                updateState(CHANNEL_SERVICE, new StringType(cpname));
            }

            if (res.getMethod().toLowerCase().equals("mediabufferstartevent")) {
                // Bug: after a Resume, the GetRadioInfo is asked too fast. This info only becomes available after some
                // seconds.
                // Intermediate respond is received with Method "MediaBufferStartEvent". Reschedule getRadioInfo request
                scheduler.execute(() -> {
                    try {
                        TimeUnit.MINUTES.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block

                    }
                    queryDeviceStatus();
                });

            }

        } else {
            logger.trace("Device is offline {} ");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, "Device could not be contacted");

        }
    }
}
