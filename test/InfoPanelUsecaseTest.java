package infopanel;

import interfaceadapter.infopanel.InfoPanelPresenter;
import interfaceadapter.infopanel.InfoPanelViewModel;
import org.junit.jupiter.api.Test;
import usecase.infopanel.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


public class InfoPanelUseCaseTest {

    @Test
    void presenter_updates_view_model_and_turns_off_loading() {
        InfoPanelViewModel vm = new InfoPanelViewModel();
        vm.loading = true;
        vm.visible = false;

        InfoPanelPresenter presenter = new InfoPanelPresenter(vm);

        InfoPanelOutputData out = new InfoPanelOutputData(
                "Mirabel",
                5.3,
                "Light rain",
                List.of(3.0, 2.0, 1.0),
                Instant.parse("2025-11-23T01:00:00Z"),
                Map.of("unit", "C")
        );

        presenter.present(out);

        assertFalse(vm.loading, "present() should set loading=false");
        assertTrue(vm.visible, "present() should make the panel visible");
        assertEquals("Mirabel", vm.placeName);
        assertEquals(5.3, vm.tempC, 1e-9);
        assertEquals("Light rain", vm.condition);
        assertNotNull(vm.fetchedAt);
        assertEquals(3, vm.hourlyTemps.size());
        assertEquals(3.0, vm.hourlyTemps.get(0), 1e-9);
    }

    @Test
    void interactor_parses_weatherapi_like_xml_and_calls_present() {
        final String xml = """
                <root>
                  <location>
                    <name>Mirabel</name>
                  </location>
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

        PointWeatherFetcher fakeFetcher = (lat, lon) -> xml;

        AtomicReference<InfoPanelOutputData> captured = new AtomicReference<>();
        InfoPanelOutputBoundary probe = new InfoPanelOutputBoundary() {
            @Override public void presentLoading() { /* no-op */ }
            @Override public void present(InfoPanelOutputData out) { captured.set(out); }
            @Override public void presentError(InfoPanelError error) {
                fail("presentError should not be called for valid XML");
            }
        };

        InfoPanelInteractor interactor = new InfoPanelInteractor(fakeFetcher, probe);

        interactor.execute(new InfoPanelInputData(45.5, -73.8, 10));

        InfoPanelOutputData out = captured.get();
        assertNotNull(out, "Interactor should call present()");

        assertEquals("Mirabel", out.placeName);
        assertEquals(5.3, out.tempC, 1e-9);
        assertEquals("Light rain", out.condition);
        assertNotNull(out.fetchedAt, "fetchedAt should be populated");
        assertNotNull(out.hourlyTemps);
        assertEquals(3, out.hourlyTemps.size());
        assertEquals(3.0, out.hourlyTemps.get(0), 1e-9);
    }
}
