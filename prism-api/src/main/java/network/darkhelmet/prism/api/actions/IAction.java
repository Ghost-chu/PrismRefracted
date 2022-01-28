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

package network.darkhelmet.prism.api.actions;

import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;

public interface IAction {
    /**
     * Apply the rollback. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     */
    void applyRollback(IActivity activityContext);

    /**
     * Apply the restore. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     */
    void applyRestore(IActivity activityContext);

    /**
     * Format the content of this action for text tdisplay.
     *
     * @return The content string
     */
    String formatContent();

    /**
     * Get the action type.
     *
     * @return The action type
     */
    ActionType type();
}
