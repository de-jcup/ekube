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
 package de.jcup.ekube.preferences;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.eclipse.commons.ui.ColorUtil;
import de.jcup.ekube.Activator;

public class EKubePreferences {

    private static EKubePreferences INSTANCE = new EKubePreferences();
    private IPreferenceStore store;

    private EKubePreferences() {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
    }

    public String getStringPreference(EKubePreferenceConstants id) {
        String data = getPreferenceStore().getString(id.getId());
        if (data == null) {
            data = "";
        }
        return data;
    }

    public boolean getBooleanPreference(EKubePreferenceConstants id) {
        boolean data = getPreferenceStore().getBoolean(id.getId());
        return data;
    }

    public void setBooleanPreference(EKubePreferenceConstants id, boolean value) {
        getPreferenceStore().setValue(id.getId(), value);
    }

    public IPreferenceStore getPreferenceStore() {
        return store;
    }

    public boolean getDefaultBooleanPreference(EKubePreferenceConstants id) {
        boolean data = getPreferenceStore().getDefaultBoolean(id.getId());
        return data;
    }

    public RGB getColor(PreferenceIdentifiable identifiable) {
        RGB color = PreferenceConverter.getColor(getPreferenceStore(), identifiable.getId());
        return color;
    }

    /**
     * Returns color as a web color in format "#RRGGBB"
     * 
     * @param identifiable
     * @return web color string
     */
    public String getWebColor(PreferenceIdentifiable identifiable) {
        RGB color = getColor(identifiable);
        if (color == null) {
            return null;
        }
        String webColor = ColorUtil.convertToHexColor(color);
        return webColor;
    }

    public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) {
        PreferenceConverter.setDefault(getPreferenceStore(), identifiable.getId(), color);
    }

    public static EKubePreferences getInstance() {
        return INSTANCE;
    }

    public File getKubeConfigFile() {
        String path = getPreferenceStore().getString(EKubePreferenceConstants.KUBE_CONFIGFILE_PATH.getId());
        return new File(path);
    }

    public List<String> getFilteredNamespacesAsList() {
        String namespaces = getPreferenceStore().getString(EKubePreferenceConstants.FILTERED_NAMESPACES.getId());
        return Arrays.asList(StringUtils.split(namespaces, ","));
    }

    public boolean getFilterNamespacesEnabled() {
        return getBooleanPreference(EKubePreferenceConstants.FILTER_NAMESPACES_ENABLED);
    }

    public boolean areExperimentalFeaturesEnabled() {
        return getBooleanPreference(EKubePreferenceConstants.ENABLE_EXPERIMENTAL_FEATURES);
    }
    
    public int getLogLinesToFetch(){
        return getPreferenceStore().getInt(EKubePreferenceConstants.LOG_LINES_TO_FETCH.getId());
    }

}
