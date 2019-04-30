/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import Json.SpriteDeserializer;
import Json.SpriteSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Class that holds all Sprite attributes  and functionality along with helper methods (get, set, modify, checkForNull) 
 * @author Kevin Lai, John Dobie, Seongyeop Jeong
 */
@Entity
@XmlRootElement
@JsonSerialize(using = SpriteSerializer.class)
@JsonDeserialize(using = SpriteDeserializer.class)
public class Sprite implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static Random random = new Random();

    final static int SIZE = 10;
    final static int MAX_SPEED = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column
    private Integer panelWidth;
    @Column
    private Integer panelHeight;
    @Column
    private Integer x;
    @Column
    private Integer y;
    @Column
    private Integer dx;
    @Column
    private Integer dy;
    @Column   
    private Color color;

    public Sprite() {
    }

    public Sprite(int height, int width) {
        this.panelWidth = width;
        this.panelHeight = height;
        x = random.nextInt(width);
        y = random.nextInt(height);
        dx = random.nextInt(2 * MAX_SPEED) - MAX_SPEED;
        dy = random.nextInt(2 * MAX_SPEED) - MAX_SPEED;
    }

    public Sprite(Integer height, Integer width, Color color) {
        this(height, width);
        this.color = color;
    }

    public Integer getPanelWidth() {
        return panelWidth;
    }

    public void setPanelWidth(Integer panelWidth) {
        this.panelWidth = panelWidth;
    }

    public Integer getPanelHeight() {
        return panelHeight;
    }

    public void setPanelHeight(Integer panelHeight) {
        this.panelHeight = panelHeight;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getDx() {
        return dx;
    }

    public void setDx(Integer dx) {
        this.dx = dx;
    }

    public Integer getDy() {
        return dy;
    }

    public void setDy(Integer dy) {
        this.dy = dy;
    }
  
    @XmlJavaTypeAdapter(ColorAdapter.class)
    public Color getColor() {
        return color;
    }
   
    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, SIZE, SIZE);
    }

    public void move() {

        // check for bounce and make the ball bounce if necessary
        //
        if (x < 0 && dx < 0) {
            //bounce off the left wall 
            x = 0;
            dx = -dx;
        }
        if (y < 0 && dy < 0) {
            //bounce off the top wall
            y = 0;
            dy = -dy;
        }
        if (x > panelWidth - SIZE && dx > 0) {
            //bounce off the right wall
            x = panelWidth - SIZE;
            dx = -dx;
        }
        if (y > panelHeight - SIZE && dy > 0) {
            //bounce off the bottom wall
            y = panelHeight - SIZE;
            dy = -dy;
        }

        //make the ball move
        x += dx;
        y += dy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sprite)) {
            return false;
        }
        Sprite other = (Sprite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Sprite[ id=" + id + " ]";
    }
        //Method used when we want to modify a sprite in the RESTful webservices
        //check for values that are not null(meaning they have been given in the body request)
        //update the existing sprite with these values given in the body request.
        public Sprite modify(Sprite existing) {
       
            if(this.panelWidth != null) {
                if(this.panelWidth>=0)
                existing.panelWidth = this.panelWidth;
            }
            
            if(this.panelHeight != null) {
                if(this.panelHeight>=0)
                existing.panelHeight = this.panelHeight;
            }
            
            if(this.x != null) {
             if(this.x>=0)
                existing.x = this.x;
            }
            if(this.y  != null) {
                if(this.y>=0)
                existing.y = this.y;
            }
            
            if(this.dx != null) existing.dx = this.dx;
            
            if(this.dy != null) existing.dy = this.dy;
            
            if(this.color != null) existing.color = this.color;
            
             return existing;
        }
        
        // Method used in cases when we want to auto generate a sprite in the RESTful web services
        // this ensures if we don't fill out values in the XML or JSON body request there will be default values
        public void checkForNull(Sprite existing) {
            if(panelHeight == null) panelHeight = 0;
            if(panelWidth == null) panelWidth = 0;
            if(x == null) x = 0;
            if(y == null) y = 0;
            if(dx == null) dx = 0;
            if(dy == null) dy = 0;
            if(color == null) color = Color.BLACK;
        }
        
        // Inner class used to map Color Object to XML representation by using a XMLAdapater
        private static class ColorAdapter extends XmlAdapter<ColorAdapter.ColorValueType, Color> {

        @Override
        public Color unmarshal(ColorValueType v) throws Exception {
            return new Color(v.red, v.green, v.blue);
        }

        @Override
        public ColorValueType marshal(Color v) throws Exception {
            return new ColorValueType(v.getRed(), v.getGreen(), v.getBlue());
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class ColorValueType {
            private int red;
            private int green;
            private int blue;

            public ColorValueType() {
            }

            public ColorValueType(int red, int green, int blue) {
                this.red = red;
                this.green = green;
                this.blue = blue;
            }
        }
    }
        
        
        
        
}

