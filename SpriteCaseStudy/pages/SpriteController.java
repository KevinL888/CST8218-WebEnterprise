package pages;

import business.Sprite;
import business.SpriteFacade;
import pages.util.JsfUtil;
import pages.util.PaginationHelper;
import java.awt.Color;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("spriteController")
@SessionScoped
public class SpriteController implements Serializable {

    private Sprite current;
    private DataModel items = null;
    @EJB
    private business.SpriteFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public SpriteController() {
    }

    public Sprite getSelected() {
        if (current == null) {
            current = new Sprite();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SpriteFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Sprite();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Sprite getSprite(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Sprite.class)
    public static class SpriteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SpriteController controller = (SpriteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "spriteController");
            return controller.getSprite(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Sprite) {
                Sprite o = (Sprite) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Sprite.class.getName());
            }
        }

    }
    
    /*
    Inner class Color Converter for JSF pages that allows us to parse java Color Objects and return String representation of a Color object
    */
    @FacesConverter("pages.SpriteController.ColorConverter")
    public static class ColorConverter implements Converter{

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
       
            if(value == null || value.length() == 0) {
                return null;
            }
            
            FacesMessage msg;
            Scanner sc = new Scanner(value);
            sc.useDelimiter("\\D+");
            int counter = 0;
            int [] RGB = new int[3];
            
            for(int i = 0; i <3 && sc.hasNext(); i++) {
                counter ++;
            RGB[i] = sc.nextInt();         
            }
            
            if(counter == 3) {        
                if(RGB[0] >= 0 && RGB[0]<=255 && RGB[1] >=0 && RGB[1]<=255 && RGB[2] >=0 && RGB[2] <=255) {
                        
                            Color color = new Color(RGB[0],RGB[1],RGB[2]);                           
                               
                            sc.close();
                            return color;
                        }
                    msg = new FacesMessage("Argument for colors must be between 0 - 255 inclusive");         
                    }
        else {  
           msg = new FacesMessage("Three RGB numbers are required to form a color.  ex. 160 205 190");
     }         
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }

        @Override
        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
            Color color = (Color) arg2;
            return "Red:"+color.getRed()+", Green:"+color.getGreen()+", Blue:"+color.getBlue();
        }
        
    }
    

}
