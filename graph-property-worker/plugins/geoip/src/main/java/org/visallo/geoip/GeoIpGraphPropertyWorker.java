package org.visallo.geoip;

import com.google.inject.Inject;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.vertexium.Element;
import org.vertexium.Property;
import org.vertexium.mutation.ElementMutation;
import org.vertexium.type.GeoPoint;
import org.visallo.core.exception.VisalloException;
import org.visallo.core.ingest.graphProperty.GraphPropertyWorkData;
import org.visallo.core.ingest.graphProperty.GraphPropertyWorker;
import org.visallo.core.ingest.graphProperty.GraphPropertyWorkerPrepareData;
import org.visallo.core.model.Description;
import org.visallo.core.model.Name;
import org.visallo.core.model.properties.types.GeoPointVisalloProperty;
import org.visallo.core.model.properties.types.PropertyMetadata;
import org.visallo.core.model.properties.types.StringVisalloProperty;
import org.visallo.core.model.properties.types.VisalloPropertyUpdate;
import org.visallo.core.util.VisalloLogger;
import org.visallo.core.util.VisalloLoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Name("GeoIP")
@Description("Geo-locate IP addresses")
public class GeoIpGraphPropertyWorker extends GraphPropertyWorker {
    private static final VisalloLogger LOGGER = VisalloLoggerFactory.getLogger(GeoIpGraphPropertyWorker.class);
    public static final String GEO_LOCATION_INTENT = "geoLocation";
    private static final Pattern IP_ADDRESS_REGEX = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    private GeoPointVisalloProperty geoLocationProperty;
    private GeoIpRepository geoIpRepository;

    @Override
    public void prepare(GraphPropertyWorkerPrepareData workerPrepareData) throws Exception {
        super.prepare(workerPrepareData);
        String geoLocationIri = getOntologyRepository().getRequiredPropertyIRIByIntent(GEO_LOCATION_INTENT);
        geoLocationProperty = new GeoPointVisalloProperty(geoLocationIri);

        GeoIpGraphPropertyWorkerConfiguration configuration = new GeoIpGraphPropertyWorkerConfiguration();
        getConfiguration().setConfigurables(configuration, GeoIpGraphPropertyWorker.class.getName());

        FileSystem fs = workerPrepareData.getHdfsFileSystem();
        Path geoLite2CityBlocksIpv4HdfsPath = new Path(configuration.getPathPrefix() + "/GeoLite2-City-Blocks-IPv4.csv");
        LOGGER.debug("Loading %s", geoLite2CityBlocksIpv4HdfsPath.toString());
        if (!fs.exists(geoLite2CityBlocksIpv4HdfsPath)) {
            throw new VisalloException("Could not find file: " + geoLite2CityBlocksIpv4HdfsPath);
        }
        try (InputStream in = fs.open(geoLite2CityBlocksIpv4HdfsPath)) {
            this.geoIpRepository.loadGeoIp(in);
        }

        Path geoLite2CityLocationsEnHdfsPath = new Path(configuration.getPathPrefix() + "/GeoLite2-City-Locations-en.csv");
        LOGGER.debug("Loading %s", geoLite2CityLocationsEnHdfsPath.toString());
        if (!fs.exists(geoLite2CityLocationsEnHdfsPath)) {
            throw new VisalloException("Could not find file: " + geoLite2CityLocationsEnHdfsPath);
        }
        try (InputStream in = fs.open(geoLite2CityLocationsEnHdfsPath)) {
            this.geoIpRepository.loadGeoLocations(in);
        }
    }

    @Override
    public void execute(InputStream in, GraphPropertyWorkData data) throws Exception {
        String propertyValue = StringVisalloProperty.getValue(data.getProperty());
        GeoPoint geoPoint = geoIpRepository.find(propertyValue);
        if (geoPoint == null) {
            return;
        }

        PropertyMetadata metadata = new PropertyMetadata(getUser(), data.getVisibilityJson(), data.getVisibility());
        List<VisalloPropertyUpdate> changedProperties = new ArrayList<>();
        ElementMutation m = data.getElement().prepareMutation();
        geoLocationProperty.updateProperty(changedProperties, data.getElement(), m, data.getProperty().getKey(), geoPoint, metadata, data.getVisibility());
        m.save(getAuthorizations());
        getGraph().flush();

        pushChangedPropertiesOnWorkQueue(data, changedProperties);
    }

    @Override
    public boolean isHandled(Element element, Property property) {
        if (property == null || geoLocationProperty == null) {
            return false;
        }

        if (!(property.getValue() instanceof String)) {
            return false;
        }
        String str = (String) property.getValue();

        if (!IP_ADDRESS_REGEX.matcher(str).matches()) {
            return false;
        }

        return true;
    }

    @Inject
    public void setGeoIpRepository(GeoIpRepository geoIpRepository) {
        this.geoIpRepository = geoIpRepository;
    }
}
