/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.listeners;

import com.google.inject.Inject;

import java.util.Optional;

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.expectations.ExpectationType;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.expectations.ExpectationsCache;
import network.darkhelmet.prism.services.filters.FilterService;
import network.darkhelmet.prism.services.recording.RecordingQueue;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class VehicleCreateListener extends AbstractListener implements Listener {
    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param actionRegistry The action registry
     * @param expectationService The expectation service
     * @param filterService The filter service
     */
    @Inject
    public VehicleCreateListener(
            ConfigurationService configurationService,
            IActionRegistry actionRegistry,
            ExpectationService expectationService,
            FilterService filterService) {
        super(configurationService, actionRegistry, expectationService, filterService);
    }

    /**
     * Listens for vehicle place events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleCreate(final VehicleCreateEvent event) {
        // Ignore if this event is disabled
        if (!configurationService.prismConfig().actions().vehiclePlace()) {
            return;
        }

        final Object cause = getCause(event.getVehicle().getLocation().getBlock().getLocation());
        final IAction action = actionRegistry.createEntityAction(ActionRegistry.VEHICLE_PLACE, event.getVehicle());

        // Build the activity
        final IActivity activity = Activity.builder()
            .action(action).location(event.getVehicle().getLocation()).cause(cause).build();

        if (filterService.allows(activity)) {
            RecordingQueue.addToQueue(activity);
        }
    }

    /**
     * Find the cause of a vehicle expectation. Checks the block (land placement)'
     * and the block below (water placement).
     *
     * @param location The location
     * @return The cause or "unknown"
     */
    private Object getCause(Location location) {
        // Query the block location from the expectation service
        ExpectationsCache expectationsCache = expectationService.cacheFor(ExpectationType.SPAWN_VEHICLE);

        Optional<Object> optionalCause = expectationsCache.expectation(location);
        if (optionalCause.isEmpty()) {
            optionalCause = expectationsCache.expectation(location.add(0, 1, 0));
        }

        // If found, mark the expectation as met
        if (optionalCause.isPresent()) {
            expectationsCache.metExpectation(location);
        }

        return optionalCause.orElse("unknown");
    }
}