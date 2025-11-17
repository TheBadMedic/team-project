package usecase.bookmark.listbookmark;

import entity.BookmarkedLocation;
import java.util.List;

/**
 * Data returned by the "list bookmarks" use case to the presenter.
 */
public final class ListBookmarksOutputData {

    private final List<BookmarkedLocation> bookmarks;

    /**
     * Constructs output data representing all persisted bookmarks.
     *
     * @param bookmarks list of bookmarks to present; a defensive copy is stored
     */
    public ListBookmarksOutputData(List<BookmarkedLocation> bookmarks) {
        this.bookmarks = List.copyOf(bookmarks);
    }

    /**
     * Returns an immutable view of the bookmarks list.
     *
     * @return list of bookmarks
     */
    public List<BookmarkedLocation> getBookmarks() {
        return bookmarks;
    }
}
