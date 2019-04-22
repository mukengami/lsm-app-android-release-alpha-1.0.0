package org.lsm.mobile.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import org.lsm.mobile.core.IEdxEnvironment;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.model.VideoModel;
import org.lsm.mobile.model.api.ProfileModel;
import org.lsm.mobile.module.prefs.UserPrefs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    protected static final Logger logger = new Logger(FileUtil.class.getName());

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    // Make this class non-instantiable
    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Utility method to determine if any removable storage (such as an SD card) is available.
     *
     * @param context The current context
     * @return True if there is removable storage available on the device.
     */
    public static boolean isRemovableStorageAvailable(@NonNull Context context) {
        return getRemovableStorageAppDir(context) != null;
    }

    /**
     * Utility method to get the removable storage directory (such as SD-Card).
     *
     * @param context The current context.
     * @return Return removable storage directory if available otherwise null.
     */
    @Nullable
    public static File getRemovableStorageAppDir(@NonNull Context context) {
        final int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            final File[] fileList = context.getExternalFilesDirs(null);
            for (File extFile : fileList) {
                if (extFile != null && Environment.isExternalStorageRemovable(extFile)) {
                    return extFile;
                }
            }
        }
        return null;
    }

    /**
     * Utility method to get the app's external storage directory (such as Phone memory).
     *
     * @param context The current context.
     * @return App's external storage directory.
     */
    @Nullable
    public static File getExternalAppDir(@NonNull Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        return (externalFilesDir != null ? externalFilesDir.getParentFile() : null);
    }

    /**
     * Returns user storage directory under /Android/data/ folder for the currently logged in user
     * or if the sd-card download is enabled the sd-card data location will be used.
     * This is the folder where all video downloads should be kept.
     *
     * @param context
     * @param environment
     * @return
     */
    @Nullable
    public static File getDownloadDirectory(Context context, IEdxEnvironment environment) {
        final File downloadDir;
        final UserPrefs userPref = environment.getUserPrefs();
        if (environment.getConfig().isDownloadToSDCardEnabled() && userPref.isDownloadToSDCardEnabled()
                && isRemovableStorageAvailable(context)) {
            downloadDir = getRemovableStorageAppDir(context);
        } else {
            // If no removable storage found, set app internal storage directory
            // as download directory
            downloadDir = getExternalAppDir(context);
        }

        final ProfileModel profile = userPref.getProfile();
        if (downloadDir != null && profile != null) {
            final File usersVideosDir = getUserVideoDirectory(downloadDir, profile.username);
            usersVideosDir.mkdirs();
            try {
                final File noMediaFile = new File(usersVideosDir, ".nomedia");
                noMediaFile.createNewFile();
            } catch (IOException ioException) {
                logger.error(ioException);
            }

            return usersVideosDir;
        }
        return null;
    }

    /**
     * Utility method to return the directory that have videos, and username hash as parent directories.
     *
     * @param downloadDir App download directory (such as Phone memory / SD-Card).
     * @param username    Current user name.
     * @return Return external directory.
     */
    public static File getUserVideoDirectory(File downloadDir, String username) {
        final File videosDir = new File(downloadDir, AppConstants.Directories.VIDEOS);
        final File usersVideosDir = new File(videosDir, Sha1Util.SHA1(username));
        return usersVideosDir;
    }

    /**
     * Utility method to return all downloaded files from the External Storage (such as Phone memory and SD-Card).
     *
     * @param context The current context.
     * @param profile Current User Profile.
     * @return Return all files from the External Storage.
     */
    public static ArrayList<File> getAllFileFromExternalStorage(Context context, ProfileModel profile) {
        final File externalAppDir = FileUtil.getUserVideoDirectory(FileUtil.getExternalAppDir(context)
                , profile.username);
        final ArrayList<File> extraFiles = new ArrayList<>();
        if (externalAppDir.exists()) {
            extraFiles.addAll(Arrays.asList(externalAppDir.listFiles()));
        }
        if (FileUtil.isRemovableStorageAvailable(context)) {
            File removableStorageAppDir = FileUtil.getUserVideoDirectory(FileUtil.getRemovableStorageAppDir(context)
                    , profile.username);
            if (removableStorageAppDir.exists()) {
                extraFiles.addAll(Arrays.asList(removableStorageAppDir.listFiles()));
            }
        }
        return extraFiles;
    }

    /**
     * Utility method to delete the extra files excluding files that paths are available in the database.
     * and '.nomedia'(restrict media players to scan the media files) file.
     *
     * @param dbEntries  Files path that are exist in database.
     * @param extraFiles Files needed to delete.
     */
    public static void deleteExtraFilesNotInDatabase(List<VideoModel> dbEntries, ArrayList<File> extraFiles) {
        boolean fileExist;
        for (File extraFile : extraFiles) {
            final String path = extraFile.getAbsolutePath();
            fileExist = false;
            for (VideoModel videoModel : dbEntries) {
                if (path.equals(videoModel.getFilePath())) {
                    fileExist = true;
                }
            }
            if (!fileExist && !path.endsWith(".nomedia")) {
                FileUtil.deleteRecursive(extraFile);
            }
        }
    }

    /**
     * Returns the text of a file as a String object
     *
     * @param context  The current context
     * @param fileName The name of the file to load from assets folder
     * @return The text content of the file
     */
    public static String loadTextFileFromAssets(Context context, String fileName)
            throws IOException {
        return getStringFromInputStream(context.getAssets().open(fileName));
    }

    /**
     * Returns the text of a file as a String object
     *
     * @param context The current context
     * @param fileId  The resource id of a file to load
     * @return The text content of the file
     */
    public static String loadTextFileFromResources(@NonNull Context context,
                                                   @RawRes int fileId) throws IOException {
        return getStringFromInputStream(context.getResources().openRawResource(fileId));
    }

    private static String getStringFromInputStream(@NonNull InputStream inputStream)
            throws IOException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                for (int n; (n = inputStream.read(buffer)) >= 0; ) {
                    outputStream.write(buffer, 0, n);
                }
                return outputStream.toString();
            } finally {
                outputStream.close();
            }
        } finally {
            inputStream.close();
        }
    }

    /**
     * Deletes a file or directory and all its content recursively.
     *
     * @param fileOrDirectory The file or directory that needs to be deleted.
     */
    public static void deleteRecursive(@NonNull File fileOrDirectory) {
        deleteRecursive(fileOrDirectory, Collections.EMPTY_LIST);
    }

    /**
     * Deletes a file or directory and all its content recursively.
     *
     * @param fileOrDirectory The file or directory that needs to be deleted.
     * @param exceptions      Names of the files or directories that need to be skipped while deletion.
     */
    public static void deleteRecursive(@NonNull File fileOrDirectory,
                                       @NonNull List<String> exceptions) {
        if (exceptions.contains(fileOrDirectory.getName())) return;

        if (fileOrDirectory.isDirectory()) {
            File[] filesList = fileOrDirectory.listFiles();
            if (filesList != null) {
                for (File child : filesList) {
                    deleteRecursive(child, exceptions);
                }
            }
        }

        // Don't break the recursion upon encountering an error
        // noinspection ResultOfMethodCallIgnored
        fileOrDirectory.delete();
    }
}
