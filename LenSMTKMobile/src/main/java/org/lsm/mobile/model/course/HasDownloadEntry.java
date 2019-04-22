package org.lsm.mobile.model.course;

import android.support.annotation.Nullable;

import org.lsm.mobile.model.db.DownloadEntry;
import org.lsm.mobile.module.storage.IStorage;

public interface HasDownloadEntry {
    @Nullable
    DownloadEntry getDownloadEntry(IStorage storage);

    @Nullable
    String getDownloadUrl();
}
