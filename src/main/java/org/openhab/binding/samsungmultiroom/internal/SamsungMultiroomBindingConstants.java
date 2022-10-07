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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SamsungMultiroomBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author WDRC - Initial contribution
 */
@NonNullByDefault
public class SamsungMultiroomBindingConstants {

    private static final String BINDING_ID = "samsungmultiroom";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_WAMPLAYER = new ThingTypeUID(BINDING_ID, "WAMPlayer");

    // List of all Channel ids
    public static final String CHANNEL_NAME = "name";
    public static final String CHANNEL_SERVICE = "service";
    public static final String CHANNEL_PLAYPRESET = "playpreset";

    public static final String CHANNEL_CONTROL = "control";
    public static final String CHANNEL_VOLUME = "volume";
    public static final String CHANNEL_MUTE = "mute";
    public static final String CHANNEL_INPUTS = "inputs";

    // TOBE public static final String CHANNEL_CURRENTPOSITION = "currentPosition";
    // public static final String CHANNEL_DURATION = "duration";

    public static final String CHANNEL_ZONENAME = "zonename";
    public static final String CHANNEL_ZONEGROUPID = "zonegroupid";
    public static final String CHANNEL_CURRENTALBUM = "currentalbum";
    public static final String CHANNEL_CURRENTALBUMART = "currentalbumart";
    public static final String CHANNEL_CURRENTALBUMARTURL = "currentalbumarturl";
    public static final String CHANNEL_CURRENTARTIST = "currentartist";
    public static final String CHANNEL_CURRENTTITLE = "currenttitle";
    public static final String CHANNEL_PLAYURI = "playuri";
    public static final String CHANNEL_STATE = "state";

    // List of all HTTP commands URLS
    public static String PLACEHOLDER_FOR_HOSTNAME = "ip_speaker";
    public static String PLACEHOLDER_FOR_VALUE = "VALUEPLACEHOLDER";
    public static String HTTPCOMMAND_GETNAME = "http://ip_speaker:55001/UIC?cmd=%3Cname%3EGetSpkName%3C/name%3E";
    public static String HTTPCOMMAND_MUTEOFF = "http://ip_speaker:55001/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetMute%3C/name%3E%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22off%22/%3E";
    public static String HTTPCOMMAND_MUTEON = "http://ip_speaker:55001/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetMute%3C/name%3E%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22on%22/%3E";
    public static String HTTPCOMMAND_GETVOLUME = "http://ip_speaker:55001/UIC?cmd=%3Cname%3EGetVolume%3C/name%3E";
    public static String HTTPCOMMAND_SETVOLUME = "http://ip_speaker:55001/UIC?cmd=%3Cname%3ESetVolume%3C/name%3E%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22VALUEPLACEHOLDER%22/%3E";
    public static String HTTPCOMMAND_GETGROUPNAME = "http://ip_speaker:55001/UIC?cmd=%3Cname%3EGetGroupName%3C/name%3E";
    public static String HTTPCOMMAND_GETRADIOINFO = "http://ip_speaker:55001/CPM?cmd=%3Cname%3EGetRadioInfo%3C/name%3E";
    public static String HTTPCOMMAND_GETMUTE = "http://ip_speaker:55001/UIC?cmd=%3Cname%3EGetMute%3C/name%3E";
    public static String HTTPCOMMAND_SETPLAYCONTROL = "http://ip_speaker:55001/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetPlaybackControl%3C/name%3E%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22VALUEPLACEHOLDER%22/%3E";
    public static String HTTPCOMMAND_GETMUSICSTATUS = "http://ip_speaker:55001/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E";
    public static String HTTPCOMMAND_GETCPLIST = "http://ip_speaker:55001/CPM?cmd=%3Cname%3EGetCpList%3C/name%3E%3Cp%20type=%22dec%22%20name=%22liststartindex%22%20val=%220%22/%3E%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2220%22/%3E";

    public static String HTTPCOMMAND_PLAYURL = "http://ip_speaker:55001/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetUrlPlayback%3C/name%3E%3Cp%20type=%22cdata%22%20name=%22url%22%20val=%22empty%22%3E%3C![CDATA[VALUEPLACEHOLDER]]%3E%3C/p%3E%3Cp%20type=%22dec%22%20name=%22buffersize%22%20val=%220%22/%3E%3Cp%20type=%22dec%22%20name=%22seektime%22%20val=%220%22/%3E%3Cp%20type=%22dec%22%20name=%22resume%22%20val=%221%22/%3E";
    public static String HTTPCOMMAND_PLAYPRESETURL = "http://ip_speaker:55001/CPM?cmd=%3Cname%3ESetPlayPreset%3C/name%3E%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22VALUEPLACEHOLDER%22/%3E%3Cp%20type=%22dec%22%20name=%22presettype%22%20val=%220%22/%3E";
    public static String HTTPCOMMAND_GETOPENLISTENER = "http://ip_speaker:55001/UIC?cmd=";
}
