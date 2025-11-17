package usecase.bookmark.listbookmark;

/**
 * Input boundary for the "list bookmarks" use case.
 * Called by the controller when the user requests to view all bookmarks.
 */
public interface ListBookmarksInputBoundary {

    /**
     * Lists all currently persisted bookmarks.
     *
     * @param inputData input data for this use case (currently unused but kept for flexibility)
     */
    void listBookmarks(ListBookmarksInputData inputData);
}
