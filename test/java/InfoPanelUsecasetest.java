package infopanel;

import interfaceadapter.infopanel.InfoPanelController;
import interfaceadapter.infopanel.InfoPanelPresenter;
import interfaceadapter.infopanel.InfoPanelViewModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import usecase.infopanel.*;

import javax.swing.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


public class InfoPanelIntegrationTest {

    private static final int SLEEP_MS = 380;

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }


    private static PointWeatherFetcher fakeFetcherWithXml(String xml) {
        return (lat, lon) -> xml;
    }

    private static String minimalXml() {
        return """
            <root>
              <location><name>Mirabel</name></location>
              <current>
                <temp_c>5.3</temp_c>
                <condition><text>Light rain</text></condition>
                <last_updated_epoch>1732323600</last_updated_epoch>
              </current>
              <forecast>
                <forecastday>
                  <hour><time_epoch>1732323600</time_epoch><temp_c>3</temp_c></hour>
                  <hour><time_epoch>1732327200</time_epoch><temp_c>2</temp_c></hour>
                  <hour><time_epoch>1732330800</time_epoch><temp_c>1</temp_c></hour>
                </forecastday>
              </forecast>
            </root>
            """;
    }

    private static class ProbePresenter extends InfoPanelPresenter {
        final AtomicReference<InfoPanelError> lastError = new AtomicReference<>();

        ProbePresenter(InfoPanelViewModel vm) {
            super(vm);
        }
        @Override public void presentError(InfoPanelError error) {
            super.presentError(error);
            lastError.set(error);
        }
    }

    private static class Rig {
        final InfoPanelViewModel vm = new InfoPanelViewModel();
        final ProbePresenter presenter = new ProbePresenter(vm);
        final InfoPanelInteractor interactor;

        final InfoPanelController controller;

        Rig(PointWeatherFetcher fetcher) {
            interactor = new InfoPanelInteractor(fetcher, presenter);
            controller = new InfoPanelController(interactor, presenter);
        }

        void triggerViewport(double lat, double lon, int zoom) {
            // Simulate InfoPanelView.onViewportChanged(...) calling controller.
            controller.onViewportChanged(lat, lon, zoom);
        }

        void waitDebounce() {
            try { Thread.sleep(SLEEP_MS); } catch (InterruptedException ignored) {}
        }
    }


    @Test
    void pops_up_and_populates_vm_when_zoom_is_above_threshold() {
        Rig rig = new Rig(fakeFetcherWithXml(minimalXml()));

        rig.triggerViewport(45.5, -73.8, 10);
        rig.waitDebounce();

        assertFalse(rig.presenter.lastError.get() == InfoPanelError.HIDDEN_BY_ZOOM,
                "Should not be hidden by zoom");
        assertFalse(rig.vm.loading, "Loading should be false after present()");
        assertTrue(rig.vm.visible, "Panel should be visible when zoom is high enough");

        assertEquals("Mirabel", rig.vm.placeName);
        assertEquals(5.3, rig.vm.tempC, 1e-9);
        assertEquals("Light rain", rig.vm.condition);
        assertNotNull(rig.vm.fetchedAt);
        assertEquals(3, rig.vm.hourlyTemps.size());
        assertEquals(3.0, rig.vm.hourlyTemps.get(0), 1e-9);
    }

    @Test
    void hidden_by_zoom_when_zoom_is_below_threshold() {
        Rig rig = new Rig(fakeFetcherWithXml(minimalXml()));

        rig.triggerViewport(45.5, -73.8, 6);
        rig.waitDebounce();

        assertEquals(InfoPanelError.HIDDEN_BY_ZOOM, rig.presenter.lastError.get(),
                "Presenter should report hidden-by-zoom");
        assertNull(rig.vm.placeName, "No data should be written when hidden by zoom");
    }

    @Test
    void closing_suppresses_same_tile_until_center_changes_tile() {
        Rig rig = new Rig(fakeFetcherWithXml(minimalXml()));

        rig.triggerViewport(45.500, -73.800, 10);
        rig.waitDebounce();
        assertTrue(rig.vm.visible);

        rig.controller.onCloseRequested();
        assertEquals(InfoPanelError.USER_CLOSED, rig.presenter.lastError.get());

        rig.triggerViewport(45.5001, -73.8001, 10);
        rig.waitDebounce();
        assertEquals(InfoPanelError.USER_CLOSED, rig.presenter.lastError.get(),
                "Same tile after closing should still be suppressed");
        assertEquals("Mirabel", rig.vm.placeName, "VM should still have last content; no repopulation");

        rig.triggerViewport(46.0, -74.3, 10);
        rig.waitDebounce();
        assertFalse(rig.vm.loading);
        assertTrue(rig.vm.visible);
        assertNotNull(rig.vm.fetchedAt);
    }

    @Test
    void interactor_parses_and_calls_present_with_expected_fields() {
        AtomicReference<InfoPanelOutputData> captured = new AtomicReference<>();
        InfoPanelOutputBoundary probe = new InfoPanelOutputBoundary() {
            @Override public void presentLoading() { /* no-op */ }
            @Override public void present(InfoPanelOutputData out) { captured.set(out); }
            @Override public void presentError(InfoPanelError error) { fail("Unexpected error: " + error); }
        };

        InfoPanelInteractor interactor = new InfoPanelInteractor(
                fakeFetcherWithXml(minimalXml()), probe);

        interactor.execute(new InfoPanelInputData(40.0, -70.0, 12));

        InfoPanelOutputData out = captured.get();
        assertNotNull(out);
        assertEquals("Mirabel", out.placeName);
        assertEquals(5.3, out.tempC, 1e-9);
        assertEquals("Light rain", out.condition);
        assertNotNull(out.fetchedAt);
        assertEquals(List.of(3.0, 2.0, 1.0), out.hourlyTemps);
    }
}
