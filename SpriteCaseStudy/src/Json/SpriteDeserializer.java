 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import business.Sprite;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;
import javax.faces.application.FacesMessage;
import javax.faces.convert.ConverterException;

/**
 *
 * Class that extends StdDeserializer and used to Deserialize a JSON Object into Sprite Object
 * @author Kevin Lai, John Dobie, Seongyeop Jeong
 */
public class SpriteDeserializer extends StdDeserializer<Sprite>{

    public SpriteDeserializer() {
        this(null);
    }
   
    public SpriteDeserializer(Class<Sprite> t) {
        super(t);
    }
    
    @Override
    public Sprite deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        // Create a Sprite object and set fields (JSON object that is sent from REST)
        Sprite sp = new Sprite();
        
        JsonNode jnode = jp.getCodec().readTree(jp);
        if(jnode.get("id") != null)
        sp.setId(jnode.get("id").asLong());
        if(jnode.get("panelWidth") != null)
        sp.setPanelWidth(jnode.get("panelWidth").asInt());
        if(jnode.get("panelHeight") != null)
        sp.setPanelHeight(jnode.get("panelHeight").asInt());
        if(jnode.get("x") != null)
        sp.setX(jnode.get("x").asInt());
        if(jnode.get("y") != null)
        sp.setY(jnode.get("y").asInt());
        if(jnode.get("dx") != null)
        sp.setDx(jnode.get("dx").asInt());
        if(jnode.get("dy") != null)
        sp.setDy(jnode.get("dy").asInt());
        if(jnode.get("color") != null)
        sp.setColor((Color)getAsObject(jnode.get("color").toString()));

        return sp;
    }

    // Method that takes String format and converts it to a Color Object
    public Color getAsObject(String value) {
       
            if(value == null || value.length() == 0) {
                return null;
            }
            
            FacesMessage msg;
            Scanner sc = new Scanner(value);
            sc.useDelimiter("\\D+");
            
            int [] RGB = new int[3];
            
            for(int i = 0; i <3 && sc.hasNext(); i++) {
            RGB[i] = sc.nextInt();         
            }
            
            if(RGB.length == 3) {        
                if(RGB[0] >= 0 && RGB[0]<=255 && RGB[1] >=0 && RGB[1]<=255 && RGB[2] >=0 && RGB[2] <=255) {
                                      
                            Color color = new Color(RGB[0],RGB[1],RGB[2]);
                                                          
                            sc.close();
                            return color;
                        }
                    msg = new FacesMessage("Argument for colors must be between 0 - 255 inclusive");         
                    }
        else {  
           msg = new FacesMessage("Three RGB numbers are required to form a color.");
     }         
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }

    }
    

