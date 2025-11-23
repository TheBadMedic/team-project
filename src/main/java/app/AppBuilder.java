package app;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

import constants.Constants;
import dataaccessinterface.BookmarkedLocationStorage;
import dataaccessobjects.InDiskBookmarkStorage;
import entity.ProgramTime;
import entity.Viewport;
import interfaceadapter.bookmark.BookmarksViewModel;
import interfaceadapter.bookmark.addbookmark.AddBookmarkController;
import interfaceadapter.bookmark.addbookmark.AddBookmarkPresenter;
import interfaceadapter.bookmark.listbookmark.ListBookmarksController;
import interfaceadapter.bookmark.listbookmark.ListBookmarksPresenter;
import interfaceadapter.bookmark.removebookmark.RemoveBookmarkController;
import interfaceadapter.bookmark.removebookmark.RemoveBookmarkPresenter;
import interfaceadapter.infopanel.InfoPanelController;
import interfaceadapter.infopanel.InfoPanelPresenter;
import interfaceadapter.infopanel.InfoPanelViewModel;
import interfaceadapter.maptime.programtime.ProgramTimeController;
import interfaceadapter.maptime.programtime.ProgramTimePresenter;
import interfaceadapter.maptime.timeanimation.TimeAnimationController;
import interfaceadapter.weatherLayers.*;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import usecase.bookmark.addbookmark.AddBookmarkInputBoundary;
import usecase.bookmark.addbookmark.AddBookmarkOutputBoundary;
import usecase.bookmark.addbookmark.AddBookmarkUseCase;
import usecase.bookmark.listbookmark.ListBookmarksInputBoundary;
import usecase.bookmark.listbookmark.ListBookmarksUseCase;
import usecase.bookmark.removebookmark.RemoveBookmarkInputBoundary;
import usecase.bookmark.removebookmark.RemoveBookmarkOutputBoundary;
import usecase.bookmark.removebookmark.RemoveBookmarkUseCase;
import usecase.infopanel.InfoPanelInteractor;
import usecase.maptime.UpdateMapTimeInputBoundary;
import usecase.weatherLayers.layers.*;
import usecase.weatherLayers.update.UpdateOverlayOutputBoundary;
import usecase.weatherLayers.update.UpdateOverlaySizeUseCase;
import usecase.weatherLayers.update.UpdateOverlayUseCase;
import usecase.maptime.UpdateMapTimeOutputBoundary;
import usecase.maptime.UpdateMapTimeUseCase;
import view.*;
import interfaceadapter.maptime.programtime.ProgramTimeViewModel;
import dataaccessobjects.CachedTileRepository;
import entity.OverlayManager;
import interfaceadapter.mapinteraction.MapViewModel;
import interfaceadapter.mapinteraction.PanAndZoomController;
import interfaceadapter.mapinteraction.PanAndZoomPresenter;
import usecase.mapinteraction.PanAndZoomUseCase;
import usecase.mapinteraction.PanAndZoomInputBoundary;

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
    private final JMapViewer mapViewer = new JMapViewer();
    // initialising core entities
    private final ProgramTime programTime = new ProgramTime(Instant.now());
    private final OverlayManager overlayManager = new OverlayManager(Constants.DEFAULT_MAP_WIDTH,
            Constants.DEFAULT_MAP_HEIGHT);
    private final Viewport viewport = new Viewport(000,000,Constants.DEFAULT_MAP_WIDTH,
            0, 6, 0, 584);
    private final BookmarkedLocationStorage bookmarkStorage = new InDiskBookmarkStorage(Constants.BOOKMARK_DATA_PATH);
    private PanAndZoomView panAndZoomView;
    private BookmarksView bookmarksView;


    public AppBuilder() {
        BorderLayout borderLayout = new BorderLayout();
        borderPanel.setLayout(borderLayout);
        borderPanel.setPreferredSize(new Dimension(Constants.DEFAULT_PROGRAM_WIDTH, Constants.DEFAULT_PROGRAM_HEIGHT));
    }

    public AppBuilder addInfoPanelView(){
//        InfoPanelView infoPanelView;
//        InfoPanelController infoPanelController;
//        InfoPanelViewModel infoPanelViewModel;
//        InfoPanelInteractor infoPanelInteractor;
//        InfoPanelPresenter infoPanelPresenter;
//        infoPanelViewModel = new InfoPanelViewModel();
//        infoPanelController = new InfoPanelController();
//        infoPanelInteractor = new InfoPanelInteractor();
//        infoPanelView = new InfoPanelView();
//        borderPanel.add(infoPanelView, BorderLayout.WEST);
        return this;
    }

    public AppBuilder addBookmarkView(){
        ListBookmarksPresenter listBookmarksPresenter;
        RemoveBookmarkOutputBoundary removeBookmarkPresenter;
        AddBookmarkOutputBoundary addBookmarkPresenter;
        ListBookmarksInputBoundary listBookmarksUseCase;
        RemoveBookmarkInputBoundary removeBookmarkUseCase;
        AddBookmarkInputBoundary addBookmarkUseCase;
        BookmarksViewModel bookmarksViewModel;
        AddBookmarkController addBookmarkController;
        RemoveBookmarkController removeBookmarkController;
        ListBookmarksController listBookmarksController;

        bookmarksViewModel = new BookmarksViewModel();
        removeBookmarkPresenter = new RemoveBookmarkPresenter(bookmarksViewModel);
        listBookmarksPresenter = new ListBookmarksPresenter(bookmarksViewModel);
        addBookmarkPresenter = new AddBookmarkPresenter(bookmarksViewModel);
        addBookmarkUseCase = new AddBookmarkUseCase(bookmarkStorage, addBookmarkPresenter);
        removeBookmarkUseCase = new RemoveBookmarkUseCase(bookmarkStorage, removeBookmarkPresenter);
        listBookmarksUseCase = new ListBookmarksUseCase(bookmarkStorage, listBookmarksPresenter);


        addBookmarkController = new AddBookmarkController(addBookmarkUseCase);
        removeBookmarkController = new RemoveBookmarkController(removeBookmarkUseCase);
        listBookmarksController = new ListBookmarksController(listBookmarksUseCase);
        bookmarksView = new BookmarksView(bookmarksViewModel, addBookmarkController, removeBookmarkController,
                listBookmarksController);
        borderPanel.add(bookmarksView, BorderLayout.EAST);

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
        borderPanel.add(changeWeatherView, BorderLayout.EAST);
        return this;
    }

    public AppBuilder createOverlayView(){
        UpdateOverlaySizeUseCase updateOverlaySizeUseCase;
        updateOverlaySizeUseCase = new UpdateOverlaySizeUseCase(overlayManager, viewport);
        UpdateOverlaySizeController sizeController = new UpdateOverlaySizeController(
                updateOverlaySizeUseCase,
                updateOverlayUseCase);
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
        mapOverlayStructure.addComponent(panAndZoomView, 1);
        mapOverlayStructure.addComponent(weatherOverlayView, 2);
        borderPanel.add(mapOverlayStructure, BorderLayout.CENTER);
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
        ChangeLayerUseCase changeLayerUseCase;
        ChangeOpacityUseCase changeOpacityUseCase;
        ChangeLayerOutputBoundary layerOutputBoundary = new WeatherLayersPresenter(weatherLayersViewModel);
        UpdateLegendOutputBoundary legendOutputBoundary = new LegendPresenter(legendViewModel);
        changeLayerUseCase = new ChangeLayerUseCase(overlayManager, layerOutputBoundary, legendOutputBoundary);
        changeOpacityUseCase = new ChangeOpacityUseCase(overlayManager);
        WeatherLayersController layersController = new WeatherLayersController(changeLayerUseCase, changeOpacityUseCase);
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
        PanAndZoomPresenter panAndZoomPresenter;
        MapViewModel mapViewModel;
        mapViewModel = new MapViewModel();
        panAndZoomView = new PanAndZoomView(mapViewModel, mapViewer);
        panAndZoomPresenter = new PanAndZoomPresenter(
                viewport,
                panAndZoomView.getMapViewer(),
                mapViewModel
        );
        panAndZoomUseCase = new PanAndZoomUseCase(viewport);
        panAndZoomController = new PanAndZoomController(
               panAndZoomUseCase,
                panAndZoomView.getMapViewer()
        );
        panAndZoomView.setController(panAndZoomController);
        viewport.getSupport().addPropertyChangeListener(evt -> {
            if (updateOverlayUseCase != null) {
                updateOverlayUseCase.update();
            }
        });

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Weather Map");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(borderPanel);
        updateOverlayUseCase.update();

        return application;
    }


}
