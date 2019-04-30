/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Json;

import business.Sprite;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.awt.Color;
import java.io.IOException;


/**
 *Class that extends StdSerializer and used to Serialize a Sprite Object into JSON Object
 * @author KevinLai, John Dobie, Seongyeop Jeong
 */
public class SpriteSerializer extends StdSerializer<Sprite>{

    public SpriteSerializer() {
        this(null);
    }
   
    public SpriteSerializer(Class<Sprite> t) {
        super(t);
    }
    
   
    @Override
    public void serialize(Sprite value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        
        //Generating a JSON Object( This is creating our JSON string representation of a Sprite Object)
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeNumberField("panelWidth", value.getPanelWidth());
        gen.writeNumberField("panelHeight", value.getPanelHeight());
        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("y", value.getY());
        gen.writeNumberField("dx", value.getDx());
        gen.writeNumberField("dy", value.getDy());        
        gen.writeStringField("color", getAsString(value.getColor()));
        gen.writeEndObject();
    }
    
    // Method that converts a Color object and returns a String representation of the Color Object
     public String getAsString(Object value) {
            Color color = (Color) value;
            return "Red:"+color.getRed()+", Green:"+color.getGreen()+", Blue:"+color.getBlue();
     }  
    
}
