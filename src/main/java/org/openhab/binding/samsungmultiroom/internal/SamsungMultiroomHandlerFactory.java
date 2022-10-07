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

import static org.openhab.binding.samsungmultiroom.internal.SamsungMultiroomBindingConstants.THING_TYPE_WAMPLAYER;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SamsungMultiroomHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author WDRC - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.samsungmultiroom", service = ThingHandlerFactory.class)
public class SamsungMultiroomHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_WAMPLAYER);
    private final Logger logger = LoggerFactory.getLogger(SamsungMultiroomHandlerFactory.class);

    private final HttpClient httpClient;

    @Activate
    public SamsungMultiroomHandlerFactory(@Reference final HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
        this.httpClient.setMaxConnectionsPerDestination(10);
        this.httpClient.setConnectTimeout(30000);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        logger.debug("search supported handler for thing '{}'", thingTypeUID);
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        logger.debug("Creating a handler for thing '{}'", thing.getUID());

        if (SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            logger.debug("Creating a SamsungMultiroomThing for thing '{}'", thing.getUID());
            return new SamsungMultiroomHandler(thing, this.httpClient);
        }

        return null;
    }
}
