//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.21 at 03:01:29 AM BRT 
//


package br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.CrudOperationType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertiesGroup;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyType;


/**
 * <p>Java class for EntityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="group" type="{http://orionsoft.net.br/entityMetadata}PropertiesGroup" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://orionsoft.net.br/entityMetadata}commonMetadataAttributes"/>
 *       &lt;attribute name="className" type="{http://orionsoft.net.br/entityMetadata}classNameType" />
 *       &lt;attribute name="runQueryOnOpen" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="crudOperations" type="{http://orionsoft.net.br/entityMetadata}crudOperationListType" />
 *       &lt;attribute name="propertiesInQueryGrid" type="{http://www.w3.org/2001/XMLSchema}IDREFS" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="entity") //<--INSERIDO PELO LUCIO. Como o elemento root xsd:element usa um type para infer�ncia, o JaxB gera o EntityType, mas n�o definir ele como Root e nem coloca no nome "entity"
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityType", propOrder = {
    "group"
})
public class EntityType {

    @XmlElement(required = true)
    protected List<PropertiesGroup> group;
    @XmlAttribute(name = "className")
    protected String className;
    @XmlAttribute(name = "runQueryOnOpen")
    protected Boolean runQueryOnOpen;
    @XmlAttribute(name = "crudOperations")
    protected List<CrudOperationType> crudOperations;
    @XmlAttribute(name = "propertiesInQueryGrid")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<PropertyType> propertiesInQueryGrid;
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
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertiesGroup }
     * 
     * 
     */
    public List<PropertiesGroup> getGroup() {
        if (group == null) {
            group = new ArrayList<PropertiesGroup>();
        }
        return this.group;
    }

    /**
     * Gets the value of the className property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the value of the className property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassName(String value) {
        this.className = value;
    }

    /**
     * Gets the value of the runQueryOnOpen property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRunQueryOnOpen() {
        if (runQueryOnOpen == null) {
            return false;
        } else {
            return runQueryOnOpen;
        }
    }

    /**
     * Sets the value of the runQueryOnOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRunQueryOnOpen(Boolean value) {
        this.runQueryOnOpen = value;
    }

    /**
     * Gets the value of the crudOperations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the crudOperations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCrudOperations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrudOperationType }
     * 
     * 
     */
    public List<CrudOperationType> getCrudOperations() {
        if (crudOperations == null) {
            crudOperations = new ArrayList<CrudOperationType>();
        }
        return this.crudOperations;
    }

    /**
     * Gets the value of the propertiesInQueryGrid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertiesInQueryGrid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertiesInQueryGrid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<PropertyType> getPropertiesInQueryGrid() {
        if (propertiesInQueryGrid == null) {
            propertiesInQueryGrid = new ArrayList<PropertyType>();
        }
        return this.propertiesInQueryGrid;
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

}
