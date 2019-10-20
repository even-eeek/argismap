
import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.GregorianCalendar;

import com.esri.runtime.ArcGISRuntime;
import com.esri.toolkit.overlays.InfoPopupOverlay;
import com.esri.toolkit.sliders.JTimeSlider;
import com.esri.toolkit.sliders.JTimeSlider.TimeMode;
import com.esri.core.map.TimeAwareLayer;
import com.esri.core.map.TimeExtent;
import com.esri.core.map.TimeOptions.Units;
import com.esri.core.portal.Portal;
import com.esri.core.portal.WebMap;
import com.esri.map.ArcGISFeatureLayer;
import com.esri.map.GroupLayer;
import com.esri.map.JMap;
import com.esri.map.Layer;
import com.esri.map.LayerEvent;
import com.esri.map.LayerInitializeCompleteEvent;
import com.esri.map.LayerInitializeCompleteListener;
import com.esri.map.LayerListEventListenerAdapter;
import com.esri.map.MapOptions;
import com.esri.map.MapOptions.MapType;

public class NewClass {

  private JFrame window;
  private JMap map;
  final InfoPopupOverlay popupOverlay = new InfoPopupOverlay();
//  private JTimeSlider timeSlider;

//  private JTimeSlider createTimeSlider(TimeAwareLayer layer) {
//    JTimeSlider jTimeSlider = new JTimeSlider();
//    jTimeSlider.setTitle("Hurricane Paths");
//    jTimeSlider.addLayer((com.esri.map.TimeAwareLayer) layer);
//    jTimeSlider.setTimeMode(TimeMode.TimeExtent);
//    jTimeSlider.setPlaybackRate(1000); // 1 second per tick
//    jTimeSlider.setVisible(false);
//    return jTimeSlider;
//  }
  
  public NewClass() {
    window = new JFrame();
    window.setSize(800, 600);
    window.setLocationRelativeTo(null); // center on screen
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.getContentPane().setLayout(new BorderLayout(0, 0));

    // dispose map just before application window is closed.
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        super.windowClosing(windowEvent);
        map.dispose();
      }
    });

    // Before this application is deployed you must register the application on 
    // http://developers.arcgis.com and set the Client ID in the application as shown 
    // below. This will license your application to use Basic level functionality.
    // 
    // If you need to license your application for Standard level functionality, please 
    // refer to the documentation on http://developers.arcgis.com
    //
    //ArcGISRuntime.setClientID("your Client ID");

    // Using MapOptions allows for a common online basemap to be chosen
//    MapOptions mapOptions = new MapOptions(MapType.TOPO);
    map = new JMap();


    // If you don't use MapOptions, use the empty JMap constructor and add a tiled layer
    //map = new JMap();
    //ArcGISTiledMapServiceLayer tiledLayer = new ArcGISTiledMapServiceLayer(
    //  "http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
    //map.getLayers().add(tiledLayer);

    // Add the JMap to the JFrame's content pane
    window.getContentPane().add(map);
    Portal portal = new Portal("https://www.arcgis.com", null);
    String itemID = "fe2f669abf174f789057b5007d9643ba";
    
    map.addMapOverlay(popupOverlay);
    
    map.getLayers().addLayerListEventListener(new LayerListEventListenerAdapter() {
        
	  @Override
	  public void multipleLayersAdded(LayerEvent event) {
		  for (Layer layer : event.getChangedLayers().values()) {
		      if (layer instanceof ArcGISFeatureLayer) {
		        popupOverlay.addLayer(layer);
		      } else if (layer instanceof GroupLayer) {
		        for (Layer groupedLayer: ((GroupLayer) layer).getLayers()) {
		          if (groupedLayer instanceof ArcGISFeatureLayer) {
		            popupOverlay.addLayer(groupedLayer);
		          }
		        }
		      }
		   }
	  	}
	      
	  @Override
	  public void layerAdded(LayerEvent event) {
	  Layer layer = event.getChangedLayer();
	 	if (layer instanceof ArcGISFeatureLayer) {
	      popupOverlay.addLayer(layer);
	    } else if (layer instanceof GroupLayer) {
	      for (Layer groupedLayer: ((GroupLayer) layer).getLayers()) {
	        if (groupedLayer instanceof ArcGISFeatureLayer) {
	          popupOverlay.addLayer(groupedLayer);
	        }
	      }
	    }
	  }
	});
    
    WebMap webMap = null;
    try {
      // create the WebMap instance
      webMap = WebMap.newInstance(itemID, portal);
      map.loadWebMap(webMap);

    } catch (Exception e) {
      // handle any exception / display to the user
    }
    
//    timeSlider = createTimeSlider(dynamicLayer);
    JTimeSlider jTimeSlider = new JTimeSlider();
	// add the time slider for example in the SOUTH (bottom) part of a JComponent with a BorderLayout
    window.getContentPane().add(jTimeSlider, BorderLayout.SOUTH);
    
    ArcGISFeatureLayer secetaLayer = new ArcGISFeatureLayer( "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/" + 
    		  "Earthquakes/Since_1970/MapServer/0");
    secetaLayer.addLayerInitializeCompleteListener(
    		  new LayerInitializeCompleteListener() {

    		  @Override
    		  public void layerInitializeComplete(LayerInitializeCompleteEvent event) {
    		    jTimeSlider.setupFromLayer((com.esri.map.TimeAwareLayer) event.getLayer());
    		    jTimeSlider.setTitle("Secete");
    		    jTimeSlider.addLayer(secetaLayer);				
    		    jTimeSlider.setTimeExtent(new TimeExtent(
    		      new GregorianCalendar(1960, 1, 1), 
    		      new GregorianCalendar(2019, 1, 1)), 
    		      10, 
    		      Units.Years);
    		  }
    		});
    map.getLayers().add(secetaLayer);

    
  }

  /**
   * Starting point of this application.
   * @param args
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          NewClass application = new NewClass();
          application.window.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
