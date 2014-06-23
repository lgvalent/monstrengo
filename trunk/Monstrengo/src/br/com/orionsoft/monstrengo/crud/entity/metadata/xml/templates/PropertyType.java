//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.21 at 03:01:29 AM BRT 
//


package br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyBooleanType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyCalendarType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyEntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyNumericType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyStringType;


/**
 * <p>Java class for PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://orionsoft.net.br/entityMetadata}commonMetadataAttributes"/>
 *       &lt;attribute name="displayFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="editMask" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="readOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="calculated" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="valuesList" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyType")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlSeeAlso({
    PropertyNumericType.class,
    PropertyEntityType.class,
    PropertyBooleanType.class,
    PropertyStringType.class,
    PropertyCalendarType.class
})
public abstract class PropertyType {

    @XmlAttribute(name = "displayFormat")
    protected String displayFormat;
    @XmlAttribute(name = "defaultValue")
    protected String defaultValue;
    @XmlAttribute(name = "editMask")
    protected String editMask;
    @XmlAttribute(name = "visible")
    protected Boolean visible;
    @XmlAttribute(name = "required")
    protected Boolean required;
    @XmlAttribute(name = "readOnly")
    protected Boolean readOnly;
    @XmlAttribute(name = "calculated")
    protected Boolean calculated;
    @XmlAttribute(name = "valuesList")
    protected String valuesList;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "hint")
    protected String hint;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "colorName")
    protected String colorName;

    /**
     * Gets the value of the displayFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayFormat() {
        return displayFormat;
    }

    /**
     * Sets the value of the displayFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayFormat(String value) {
        this.displayFormat = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the editMask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEditMask() {
        return editMask;
    }

    /**
     * Sets the value of the editMask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditMask(String value) {
        this.editMask = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isVisible() {
        if (visible == null) {
            return true;
        } else {
            return visible;
        }
    }

    /**
     * Sets the value of the visible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVisible(Boolean value) {
        this.visible = value;
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRequired() {
        if (required == null) {
            return false;
        } else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the readOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReadOnly() {
        if (readOnly == null) {
            return false;
        } else {
            return readOnly;
        }
    }

    /**
     * Sets the value of the readOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    /**
     * Gets the value of the calculated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCalculated() {
        if (calculated == null) {
            return false;
        } else {
            return calculated;
        }
    }

    /**
     * Sets the value of the calculated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCalculated(Boolean value) {
        this.calculated = value;
    }

    /**
     * Gets the value of the valuesList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValuesList() {
        return valuesList;
    }

    /**
     * Sets the value of the valuesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValuesList(String value) {
        this.valuesList = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        if (label == null) {
            return "";
        } else {
            return label;
        }
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the hint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHint() {
        if (hint == null) {
            return "";
        } else {
            return hint;
        }
    }

    /**
     * Sets the value of the hint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHint(String value) {
        this.hint = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        if (description == null) {
            return "";
        } else {
            return description;
        }
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the colorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * Sets the value of the colorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorName(String value) {
        this.colorName = value;
    }
    
    @Override
    public String toString() {
    	return this.name;
    }

}