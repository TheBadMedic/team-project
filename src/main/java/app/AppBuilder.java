package app;


import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import view.SearchBarView;
import interfaceadapter.searchbar.SearchBarController;
import interfaceadapter.searchbar.SearchBarPresenter;
import interfaceadapter.searchbar.SearchBarViewModel;
import usecase.searchbar.SearchBarUsecase;
import dataaccessobjects.OpenWeatherGeocodingAPI;
import dataaccessinterface.GeocodingAPI;

import constants.Constants;
import dataaccessinterface.BookmarkedLocationStorage;
import dataaccessobjects.InDiskBookmarkStorage;

import entity.ProgramTime;
import entity.Viewport;
import entity.Location;
import interfaceadapter.bookmark.visitbookmark.VisitBookmarkController;
import interfaceadapter.bookmark.visitbookmark.VisitBookmarkPresenter;
import interfaceadapter.weatherlayers.legend.LegendPresenter;
import interfaceadapter.weatherlayers.legend.LegendViewModel;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlayController;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlayPresenter;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlaySizeController;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlayViewModel;
import interfaceadapter.weatherlayers.layers.WeatherLayersController;
import interfaceadapter.weatherlayers.layers.WeatherLayersPresenter;
import interfaceadapter.weatherlayers.layers.WeatherLayersViewModel;
import usecase.bookmark.visitbookmark.VisitBookmarkInputBoundary;
import usecase.bookmark.visitbookmark.VisitBookmarkOutputBoundary;
import usecase.bookmark.visitbookmark.VisitBookmarkUseCase;
import interfaceadapter.bookmark.BookmarksViewModel;
import interfaceadapter.bookmark.addbookmark.AddBookmarkController;
import interfaceadapter.bookmark.addbookmark.AddBookmarkPresenter;
import interfaceadapter.bookmark.listbookmark.ListBookmarksController;
import interfaceadapter.bookmark.listbookmark.ListBookmarksPresenter;
import interfaceadapter.bookmark.removebookmark.RemoveBookmarkController;
import interfaceadapter.bookmark.removebookmark.RemoveBookmarkPresenter;
import interfaceadapter.maptime.programtime.ProgramTimeController;
import interfaceadapter.maptime.programtime.ProgramTimePresenter;
import interfaceadapter.maptime.timeanimation.TimeAnimationController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import usecase.bookmark.addbookmark.AddBookmarkInputBoundary;
import usecase.bookmark.addbookmark.AddBookmarkOutputBoundary;
import usecase.bookmark.addbookmark.AddBookmarkUseCase;
import usecase.bookmark.listbookmark.ListBookmarksInputBoundary;
import usecase.bookmark.listbookmark.ListBookmarksUseCase;
import usecase.bookmark.removebookmark.RemoveBookmarkInputBoundary;
import usecase.bookmark.removebookmark.RemoveBookmarkOutputBoundary;
import usecase.bookmark.removebookmark.RemoveBookmarkUseCase;
import usecase.maptime.UpdateMapTimeInputBoundary;
import usecase.weatherlayers.layers.*;
import usecase.weatherlayers.updateoverlay.UpdateOverlayOutputBoundary;
import usecase.weatherlayers.updateoverlay.UpdateOverlaySizeUseCase;
import usecase.weatherlayers.updateoverlay.UpdateOverlayUseCase;
import usecase.maptime.UpdateMapTimeOutputBoundary;
import usecase.maptime.UpdateMapTimeUseCase;
import view.*;
import interfaceadapter.maptime.programtime.ProgramTimeViewModel;
import dataaccessobjects.CachedTileRepository;
import entity.OverlayManager;
import interfaceadapter.mapnavigation.MapViewModel;
import interfaceadapter.mapnavigation.PanAndZoomController;
import interfaceadapter.mapnavigation.PanAndZoomPresenter;
import usecase.mapnavigation.PanAndZoomUseCase;
import usecase.mapnavigation.PanAndZoomInputBoundary;
import dataaccessinterface.SavedMapOverlaySettings;
import dataaccessobjects.InDiskMapOverlaySettingsStorage;
import interfaceadapter.mapsettings.loadmapsettings.AutoLoadMapSettingsPresenter;
import interfaceadapter.mapsettings.loadmapsettings.LoadMapSettingsController;
import interfaceadapter.mapsettings.savemapsettings.SaveMapSettingsController;
import usecase.mapsettings.loadmapsettings.LoadMapSettingsInputBoundary;
import usecase.mapsettings.loadmapsettings.LoadMapSettingsUseCase;
import usecase.mapsettings.savemapsettings.SaveMapSettingsInputBoundary;
import usecase.mapsettings.savemapsettings.SaveMapSettingsUseCase;
import entity.WeatherType;

import static constants.Constants.SEARCH_BAR_PRFFERDSIZE_HEIGHT;
import static constants.Constants.SEARCH_BAR_PRFFERDSIZE_WIDTH;

public class AppBuilder {
    private final JPanel borderPanel = new JPanel();
    private DisplayOverlayView weatherOverlayView;
    private ProgramTimeView programTimeView;
    private ProgramTimeViewModel programTimeViewModel;
    private UpdateOverlayViewModel overlayViewModel;
    private LegendViewModel legendViewModel;
    private UpdateOverlayUseCase updateOverlayUseCase;
    private WeatherLayersViewModel weatherLayersViewModel;
    private ChangeWeatherLayersView changeWeatherView;
    private ChangeLayerUseCase changeLayerUseCase;
    private final JMapViewer mapViewer = new JMapViewer();

    // initialising core entities
    private final ProgramTime programTime = new ProgramTime(Instant.now());
    private final OverlayManager overlayManager = new OverlayManager(Constants.DEFAULT_MAP_WIDTH,
            Constants.DEFAULT_MAP_HEIGHT);
    private final Viewport viewport = new Viewport(
            000,000,Constants.DEFAULT_MAP_WIDTH,
            0, 6, 0, 584);
    private final BookmarkedLocationStorage bookmarkStorage = new InDiskBookmarkStorage(Constants.BOOKMARK_DATA_PATH);
    private final SavedMapOverlaySettings mapSettingsStorage = new InDiskMapOverlaySettingsStorage(
            Constants.MAP_SETTINGS_DATA_PATH);
    private PanAndZoomView panAndZoomView;
    private PanAndZoomPresenter panAndZoomPresenter;
    private BookmarksView bookmarksView;
    private SearchBarView searchBarView;
    private AddBookmarkInputBoundary addBookmarkUseCase;
    private RemoveBookmarkInputBoundary removeBookmarkUseCase;
    private ListBookmarksInputBoundary listBookmarksUseCase;
    private AddBookmarkOutputBoundary addBookmarkPresenter;
    private RemoveBookmarkOutputBoundary removeBookmarkPresenter;
    private ListBookmarksPresenter listBookmarksPresenter;
    private VisitBookmarkInputBoundary visitBookmarkUseCase;
    private VisitBookmarkOutputBoundary visitBookmarkPresenter;
    private VisitBookmarkController visitBookmarkController;

    private LoadMapSettingsInputBoundary loadMapSettingsUseCase;
    private SaveMapSettingsInputBoundary saveMapSettingsUseCase;
    private LoadMapSettingsController loadMapSettingsController;
    private SaveMapSettingsController saveMapSettingsController;


    public AppBuilder() {
        BorderLayout borderLayout = new BorderLayout();
        borderPanel.setLayout(borderLayout);
        borderPanel.setPreferredSize(new Dimension(Constants.DEFAULT_PROGRAM_WIDTH, Constants.DEFAULT_PROGRAM_HEIGHT));
    }

//    public AppBuilder addInfoPanelView(){
//        infoPanelViewModel = new InfoPanelViewModel();
//        infoPanelController = new InfoPanelController();
//        infoPanelUseCase = new InfoPanelInteractor();
//        infoPanelView = new InfoPanelView();
//        borderPanel.add(bookmarksView, BorderLayout.WEST);
//        return this;
//    }
public AppBuilder addSearchBarView() {
    SearchBarViewModel viewModel = new SearchBarViewModel();
    GeocodingAPI api= new OpenWeatherGeocodingAPI();
    SearchBarPresenter presenter = new SearchBarPresenter(viewModel);
    usecase.searchbar.SearchBarUsecase usecase = new SearchBarUsecase(api, presenter);
    SearchBarController controller = new SearchBarController(usecase);
    searchBarView= new SearchBarView(viewModel, controller, mapViewer);
    searchBarView.setPreferredSize(new Dimension(SEARCH_BAR_PRFFERDSIZE_WIDTH, SEARCH_BAR_PRFFERDSIZE_HEIGHT));

    return this;
}

    public AppBuilder addBookmarkView(){
        BookmarksViewModel bookmarksViewModel;

        bookmarksViewModel = new BookmarksViewModel();
        removeBookmarkPresenter = new RemoveBookmarkPresenter(bookmarksViewModel);
        listBookmarksPresenter = new ListBookmarksPresenter(bookmarksViewModel);
        addBookmarkPresenter = new AddBookmarkPresenter(bookmarksViewModel);

        addBookmarkUseCase = new AddBookmarkUseCase(bookmarkStorage, addBookmarkPresenter);
        removeBookmarkUseCase = new RemoveBookmarkUseCase(bookmarkStorage, removeBookmarkPresenter);
        listBookmarksUseCase = new ListBookmarksUseCase(bookmarkStorage, listBookmarksPresenter);

        AddBookmarkController addBookmarkController =
                new AddBookmarkController(addBookmarkUseCase);
        RemoveBookmarkController removeBookmarkController =
                new RemoveBookmarkController(removeBookmarkUseCase);
        ListBookmarksController listBookmarksController =
                new ListBookmarksController(listBookmarksUseCase);

        visitBookmarkPresenter = new VisitBookmarkPresenter(bookmarksViewModel);
        visitBookmarkUseCase = new VisitBookmarkUseCase(
                viewport,
                updateOverlayUseCase,
                panAndZoomPresenter,
                visitBookmarkPresenter
        );
        visitBookmarkController = new VisitBookmarkController(visitBookmarkUseCase);

        bookmarksView = new BookmarksView(
                bookmarksViewModel,
                addBookmarkController,
                removeBookmarkController,
                listBookmarksController,
                visitBookmarkController
        );

        bookmarksView.setPreferredSize(new Dimension(260, 0));

        return this;
    }



    public AppBuilder addProgramTimeView() {
        programTimeViewModel = new ProgramTimeViewModel();
        programTimeView = new ProgramTimeView(programTimeViewModel);
        borderPanel.add(programTimeView, BorderLayout.SOUTH);
        return this;
    }

    public AppBuilder addChangeOpacityView(){
        weatherLayersViewModel = new WeatherLayersViewModel(0.5);
        changeWeatherView = new ChangeWeatherLayersView(weatherLayersViewModel, mapViewer);
        return this;
    }
    
    /**
     * Creates a combined side panel that contains both the map settings/opacity view
     * and the bookmarks view stacked vertically.
     * 
     * @return this AppBuilder instance
     */
    public AppBuilder addSettingsAndBookmarkSidePanel() {
        BookmarkAndMapSettingsStructureView bookmarkAndSettingsStructure;
        bookmarkAndSettingsStructure = new BookmarkAndMapSettingsStructureView();
        if (searchBarView != null) {
            bookmarkAndSettingsStructure.addComponent(searchBarView);
        }

        
        // Add the map settings/opacity view first (on top)
        if (changeWeatherView != null) {
            bookmarkAndSettingsStructure.addComponent(changeWeatherView);
        }
        
        // Add the bookmarks view below it
        if (bookmarksView != null) {
            bookmarkAndSettingsStructure.addComponent(bookmarksView);
        }
        
        // Combined structure to the east side of the border panel
        borderPanel.add(bookmarkAndSettingsStructure, BorderLayout.EAST, BoxLayout.Y_AXIS);
        
        return this;
    }

    public AppBuilder createOverlayView(){
        UpdateOverlaySizeUseCase updateOverlaySizeUseCase;
        updateOverlaySizeUseCase = new UpdateOverlaySizeUseCase(overlayManager, viewport);
        UpdateOverlaySizeController sizeController = new UpdateOverlaySizeController(
                updateOverlaySizeUseCase, updateOverlayUseCase);
        weatherOverlayView = new DisplayOverlayView(sizeController, overlayViewModel);
        return this;
    }
    /**
     * Overlays the weather overlay component and JMV component. Should be called after both layers have been
     * initiazlized.
     * @return this
     */
    public AppBuilder addMapOverlayView(){
        MapOverlayStructureView mapOverlayStructure;
        
        mapOverlayStructure = new MapOverlayStructureView();
        mapOverlayStructure.addPropertyChangeListener(weatherOverlayView);
        mapOverlayStructure.addPropertyChangeListener(panAndZoomView);
        mapOverlayStructure.addComponent(panAndZoomView, 1);
        mapOverlayStructure.addComponent(weatherOverlayView, 2);
        borderPanel.add(mapOverlayStructure, BorderLayout.CENTER);
        mapOverlayStructure.fireSizeChange();
        return this;
    }

    public AppBuilder addLegendView(){
        LegendsView legendsView;
        legendViewModel = new LegendViewModel();
        legendsView = new LegendsView(legendViewModel);
        borderPanel.add(legendsView, BorderLayout.NORTH);
        return this;
    }

    public AppBuilder addWeatherLayersUseCase(){
        ChangeOpacityUseCase changeOpacityUseCase;
        ChangeLayerOutputBoundary layerOutputBoundaryWrapper;
        ChangeLayerOutputBoundary baseLayerPresenter = new WeatherLayersPresenter(weatherLayersViewModel);
        // Wrap with a presenter that saves settings when layer changes
        layerOutputBoundaryWrapper = data -> {
            baseLayerPresenter.updateOpacity(data);
            // Save settings after layer change
            if (saveMapSettingsController != null) {
                saveCurrentMapSettings();
            }
        };
        UpdateLegendOutputBoundary legendOutputBoundary = new LegendPresenter(legendViewModel);
        changeLayerUseCase = new ChangeLayerUseCase(overlayManager, layerOutputBoundaryWrapper, legendOutputBoundary);
        changeOpacityUseCase = new ChangeOpacityUseCase(overlayManager);
        WeatherLayersController layersController = new WeatherLayersController(
                changeLayerUseCase, changeOpacityUseCase);
        changeWeatherView.addLayerController(layersController);
        UpdateOverlayController updateCont = new UpdateOverlayController(updateOverlayUseCase);
        changeWeatherView.addUpdateController(updateCont);
        viewport.addListener(updateCont);
        
        return this;
    }

    public AppBuilder addUpdateOverlayUseCase(){
        overlayViewModel = new UpdateOverlayViewModel();
        final UpdateOverlayOutputBoundary output = new UpdateOverlayPresenter(overlayViewModel);
         updateOverlayUseCase = new UpdateOverlayUseCase(
                overlayManager,
                CachedTileRepository.getInstance(),
                programTime,
                 viewport,
                 output
        );
        return this;
    }

    public AppBuilder addUpdateMapTimeUseCase() {
        final UpdateMapTimeOutputBoundary updateMapTimeOutputBoundary = new ProgramTimePresenter(programTimeViewModel);
        final UpdateMapTimeInputBoundary updateMapTimeInputBoundary =
                new UpdateMapTimeUseCase(
                        programTime,
                        updateOverlayUseCase,
                        updateMapTimeOutputBoundary
                    );
        ProgramTimeController programTimeController = new ProgramTimeController(updateMapTimeInputBoundary,
                Constants.API_MAX_DAY_LIMIT_DURATION);
        TimeAnimationController timeAnimationController = new TimeAnimationController(updateMapTimeInputBoundary,
                Constants.TICK_LENGTH_MS);
        programTimeView.setProgramTimeController(programTimeController);
        programTimeView.setTimeAnimationController(timeAnimationController);
        return this;
    }
    public AppBuilder addPanZoomView() {
        PanAndZoomController panAndZoomController;
        PanAndZoomInputBoundary panAndZoomUseCase;
        MapViewModel mapViewModel;
        mapViewModel = new MapViewModel();
        panAndZoomView = new PanAndZoomView(mapViewModel, mapViewer);
        panAndZoomPresenter = new PanAndZoomPresenter(
                viewport,
                panAndZoomView.getMapViewer(),
                mapViewModel
        );
        panAndZoomUseCase = new PanAndZoomUseCase(viewport,panAndZoomPresenter);
        panAndZoomController = new PanAndZoomController(
               panAndZoomUseCase,
                panAndZoomView.getMapViewer()
        );
        panAndZoomView.setController(panAndZoomController);
        viewport.getSupport().addPropertyChangeListener(evt -> {
            // Refresh weather overlay when the viewport changes.
            if (updateOverlayUseCase != null) {
                updateOverlayUseCase.update();
            }
            // Keep the bookmark Lat/Lon fields in sync with the viewport centre.
            syncLatLonFieldsToViewport();
            
            // Save settings when viewport changes
            if ("viewportUpdated".equals(evt.getPropertyName()) && saveMapSettingsController != null) {
                saveCurrentMapSettings();
            }
        });

        return this;
    }
    
    /**
     * Sets up map settings persistence (save/load).
     */
    public AppBuilder addMapSettingsPersistence() {
        AutoLoadMapSettingsPresenter autoLoadPresenter = new AutoLoadMapSettingsPresenter(
                viewport,
                changeLayerUseCase,
                mapViewer,
                changeWeatherView
        );
        
        loadMapSettingsUseCase = new LoadMapSettingsUseCase(mapSettingsStorage, autoLoadPresenter);
        saveMapSettingsUseCase = new SaveMapSettingsUseCase(mapSettingsStorage, 
                new interfaceadapter.mapsettings.savemapsettings.SaveMapSettingsPresenter(
                        new interfaceadapter.mapsettings.MapSettingsViewModel()));
        
        loadMapSettingsController = new LoadMapSettingsController(loadMapSettingsUseCase);
        saveMapSettingsController = new SaveMapSettingsController(saveMapSettingsUseCase);
        
        return this;
    }
    
    /**
     * Saves the current map settings (viewport and weather layer).
     */
    private void saveCurrentMapSettings() {
        if (saveMapSettingsController == null) {
            return;
        }
        
        Location center = viewport.getCentre();
        if (center != null) {
            WeatherType currentWeatherType = overlayManager.getSelected();
            saveMapSettingsController.saveMapSettings(
                    center.getLatitude(),
                    center.getLongitude(),
                    viewport.getZoomLevel(),
                    currentWeatherType
            );
        }
    }

    /**
     * Syncs the bookmark input fields with the current viewport position.
     * Assumes the viewport can provide its centre as a Location.
     */
    private void syncLatLonFieldsToViewport() {
        if (bookmarksView == null) {
            return;
        }

        Location centre = viewport.getCentre();

        if (centre != null) {
            bookmarksView.setCoordinates(
                    centre.getLatitude(),
                    centre.getLongitude()
            );
        }
    }


    public JFrame build() {
        final JFrame application = new JFrame("Weather Map");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(borderPanel);
        
        // Load saved map settings on startup
        if (loadMapSettingsController != null) {
            loadMapSettingsController.loadMapSettings();
        }
        
        // Save map settings when window is closing
        application.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (saveMapSettingsController != null) {
                    saveCurrentMapSettings();
                }
            }
        });
        
        updateOverlayUseCase.update();

        return application;
    }


}
