/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.assets;

import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetData;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManagerImpl;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AssetHelper {
    protected ModuleAwareAssetTypeManager assetTypeManager;
    protected static String[] folders_;

    public AssetHelper() {
    }

    public void init(ModuleEnvironment environment) {
        assetTypeManager = new ModuleAwareAssetTypeManagerImpl();
        assetTypeManager.switchEnvironment(environment);
    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type, String regex) {
        Set<ResourceUrn> finalList = new HashSet<>();

        Set<ResourceUrn> resourceList = assetTypeManager.getAssetManager().getAvailableAssets(type);
        for (ResourceUrn resourceUrn : resourceList) {
            if (resourceUrn.toString().matches(regex)) {
                finalList.add(resourceUrn);
            }
        }

        return finalList;
    }

    public void setFolders(String... folders) {
        folders_ = folders;
    }

    public String resolveToPath(List<AssetDataFile> assetDataFiles) {
        for (AssetDataFile assetDataFile : assetDataFiles) {
            List<String> folders = assetDataFile.getPath();

            boolean validPath = true;
            if (folders_ != null) {
                for (int i = 0; i < folders_.length; i++) {
                    if (!folders_[i].equals(folders.get(folders.size() - i - 1))) {
                        validPath = false;
                        break;
                    }
                }
            }
            if (!validPath) {
                continue;
            }

            StringBuilder path = new StringBuilder();

            if (folders.get(0).equals("assets")) {
                path.append("assets/");
            } else {
                path.append("modules/").append(folders.get(0)).append("/");
            }

            for (int i = 1; i < folders.size(); i++) {
                path.append(folders.get(i)).append("/");
            }

            path.append(assetDataFile.getFilename());

            return path.toString();
        }

        throw new RuntimeException("Could not resolve path!");
    }
}
