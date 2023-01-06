package br.com.orionsoft.monstrengo.crud.labels.entities;

import javax.persistence.*;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;


/**
 * Esta classe permite que o operador crie diversos modelos de
 * etiquetas com diferentes medidas. 
 * As propriedades são interpretadas de acordo com o tipo de 
 * etiqueta. 
 * Se for do TYPE_MATRIX, as propriedades serão interpretadas 
 * na unidade de caracteres. 
 * Se for do TYPE_LASER, as propriedades serão interpretadas 
 * na unidade de milímetros.
 * 
 * @hibernate.class table="framework_label_model"
 */
@Entity
@Table(name="framework_label_model")
public class ModelLabel {
	public static final String NAME = "name";
	
	public static final String FONT_NAME = "fontName";
	public static final String FONT_SIZE = "fontSize";
	
	public static final String LINES_LABEL = "linesLabel";
	public static final String COLUMNS_LABEL = "columnsLabel";
	public static final String MARGIN_TOP = "marginTop";
	public static final String MARGIN_LEFT = "marginLeft";
	public static final String HORIZONTAL_DISTANCE = "horizontalDistance";
	public static final String VERTICAL_DISTANCE = "verticalDistance";
	public static final String LABEL_WIDTH = "labelWidth";
	public static final String LABEL_HEIGHT = "labelHeight";
	public static final String PAGE_WIDTH = "pageWidth";
	public static final String PAGE_HEIGHT = "pageHeight";
	
	public static final String ENVELOPE = "envelope";
	
	/* Propriedades de um campo */
	private long id = IDAO.ENTITY_UNSAVED;
	
	private String name;
	private String fontName;
	private int fontSize=10;

	private boolean envelope=false;
	
	private float marginTop; 
	private float marginLeft; 
	private float horizontalDistance; 
	private float verticalDistance; 
	private float labelWidth; 
	private float labelHeight; 
	private float pageWidth; 
	private float pageHeight; 

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}

    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getFontName() {return fontName;}
	public void setFontName(String fontName) {this.fontName = fontName;}
	
    /**
     * @hibernate.property
     */
	@Column
	public int getFontSize() {return fontSize;}
	public void setFontSize(int fontSize) {this.fontSize = fontSize;}
	
    /**
     * Calculado e readOnly
     */
	@Transient
	public int getLinesLabel(){
		if(labelHeight>0)
			return (int)Math.floor((pageHeight - marginTop +  verticalDistance) / (labelHeight + verticalDistance));
		return 1;
	}
	
    /**
     * Calculado e readOnly
     */
	@Transient
	public int getColumnsLabel(){
		if(labelWidth>0)
			return (int)Math.floor((pageWidth - marginLeft + horizontalDistance) / (labelWidth + horizontalDistance));
		
		return 1;
	}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getMarginTop(){return marginTop;}
	public void setMarginTop(float marginTop){this.marginTop = marginTop;} 

    /**
     * @hibernate.property
     */
	@Column
	public float getMarginLeft(){return marginLeft;}
	public void setMarginLeft(float marginLeft){this.marginLeft = marginLeft;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getHorizontalDistance(){return horizontalDistance;}
	public void setHorizontalDistance(float horizontalDistance){this.horizontalDistance = horizontalDistance;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getVerticalDistance(){return verticalDistance;}
	public void setVerticalDistance(float verticalDistance){this.verticalDistance = verticalDistance;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getLabelWidth(){return labelWidth;}
	public void setLabelWidth(float labelWidth){this.labelWidth = labelWidth;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getLabelHeight(){return labelHeight;}
	public void setLabelHeight(float labelHeight){this.labelHeight = labelHeight;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getPageWidth(){return pageWidth;}
	public void setPageWidth(float pageWidth){this.pageWidth = pageWidth;}
	
    /**
     * @hibernate.property
     */
	@Column
	public float getPageHeight(){return pageHeight;}
	public void setPageHeight(float pageHeight){this.pageHeight = pageHeight;}
	
    /**
     * @hibernate.property
     */
	@Column
	public boolean isEnvelope() {return envelope;}
	public void setEnvelope(boolean envelope) {this.envelope = envelope;}

	public String toString(){
		return this.name;
	}
	
}
