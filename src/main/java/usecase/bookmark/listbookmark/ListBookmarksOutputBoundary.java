package usecase.bookmark.listbookmark;

/**
 * Output boundary for the "list bookmarks" use case.
 * Implemented by the presenter.
 */
public interface ListBookmarksOutputBoundary {

    /**
     * Called when bookmarks have been successfully loaded.
     *
     * @param outputData data describing the list of bookmarks to present
     */
    void presentBookmarks(ListBookmarksOutputData outputData);

    /**
     * Called when listing bookmarks fails (e.g., persistence error).
     *
     * @param errorMessage a user-friendly error message
     */
    void presentListBookmarksFailure(String errorMessage);
}
