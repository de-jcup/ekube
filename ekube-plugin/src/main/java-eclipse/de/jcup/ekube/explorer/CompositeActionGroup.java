/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.explorer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

/* adopted from org.eclipse.jdt.internal.ui.actions */
public class CompositeActionGroup extends ActionGroup {

    private ActionGroup[] fGroups;

    public CompositeActionGroup() {
    }

    public CompositeActionGroup(ActionGroup[] groups) {
        setGroups(groups);
    }

    protected void setGroups(ActionGroup[] groups) {
        Assert.isTrue(fGroups == null);
        Assert.isNotNull(groups);
        fGroups = groups;
    }

    public void addGroup(ActionGroup group) {
        if (fGroups == null) {
            fGroups = new ActionGroup[] { group };
        } else {
            ActionGroup[] newGroups = new ActionGroup[fGroups.length + 1];
            System.arraycopy(fGroups, 0, newGroups, 0, fGroups.length);
            newGroups[fGroups.length] = group;
            fGroups = newGroups;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fGroups == null)
            return;
        for (int i = 0; i < fGroups.length; i++) {
            fGroups[i].dispose();
        }
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        if (fGroups == null)
            return;
        for (int i = 0; i < fGroups.length; i++) {
            fGroups[i].fillActionBars(actionBars);
        }
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        super.fillContextMenu(menu);
        if (fGroups == null)
            return;
        for (int i = 0; i < fGroups.length; i++) {
            fGroups[i].fillContextMenu(menu);
        }
    }

    @Override
    public void setContext(ActionContext context) {
        super.setContext(context);
        if (fGroups == null)
            return;
        for (int i = 0; i < fGroups.length; i++) {
            fGroups[i].setContext(context);
        }
    }

    @Override
    public void updateActionBars() {
        super.updateActionBars();
        if (fGroups == null)
            return;
        for (int i = 0; i < fGroups.length; i++) {
            fGroups[i].updateActionBars();
        }
    }
}
