package br.com.orionsoft.monstrengo.crud.entity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * Esta classe serve como Utils para converter valores (Ex: Long, Integer, BigDecimal, Boolean)
 * para String e vice-versa (String para valores).
 * 
 * @author estagio
 *
 */
public class PropertyValueFormat
{
	
	public static final String HTML_EDIT_MASK = "HTML";
    /**
     * <p>Seta uma String como um Objeto.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToStringValue(IPropertyValue prop, String value) throws PropertyValueException{
        String mask = prop.getPropetyOwner().getInfo().getEditMask();
        try
        {
            if(prop.getPropetyOwner().getInfo().isHasEditMask() && !mask.equals(HTML_EDIT_MASK)){
                MaskFormatter mf = new MaskFormatter(mask);
                mf.setValueContainsLiteralCharacters(false);
                prop.setAsObject(mf.stringToValue(value));
            }else{
            	prop.setAsObject(value);
            }
                
        } catch (ParseException e)
        {
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INVALID_STRING_VALUE", prop.getAsObject(), mask));
        }
    }
    
    /**
     * Seta uma String formatada como data como um Objeto do tipo Calendar.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToCalendar(IPropertyValue prop, String value) throws PropertyValueException{
        String mask = prop.getPropetyOwner().getInfo().getEditMask();
        try
        {
            /* Cria uma nova inst�ncia para permitir ter o newValue e oldValue*/
        	Calendar cal = Calendar.getInstance();
            
            cal.setTime(new SimpleDateFormat(mask).parse(value));

            prop.setAsCalendar(cal);

        } catch (ParseException e)
        {
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INVALID_STRING_VALUE", prop.getAsObject(), mask));
        }
    }
    
    /**
     * Seta uma String que cont�m um ou v�rios valores em BigDecimal como um Objeto do tipo BigDecimal.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToBigDecimal(IPropertyValue prop, String value) throws PropertyValueException{
        //Retira os separadores de milhar
        String temp = value.replace(".", "");
        // Converte virgula em ponto decimal
        temp = temp.replace(",", ".");
        
        if(prop.getPropetyOwner().getInfo().isCollection()){
        	prop.getAsPrimitiveCollection().clear();
        	String[] values = temp.split(";");
        	for(String str: values)
        		prop.getAsPrimitiveCollection().add(new BigDecimal(str));
        }else{
        	prop.setAsBigDecimal(new BigDecimal(temp));
        }
        
    }
    
    /**
     * Seta uma String que cont�m um valor boolean como um Objeto do tipo Boolean. 
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToBoolean(IPropertyValue prop, String value) throws PropertyValueException{
        String mask = prop.getPropetyOwner().getInfo().getEditMask();
        
        try{
            //se n�o exitir a m�scara, seta o que est� no value
            if (StringUtils.isEmpty(mask)){
            	prop.setAsBoolean(Boolean.parseBoolean(value));
            }
            else{ //sen�o usa a m�scara encontrada
                String[] list = StringUtils.split(mask,",");
                if (value.equals(list[0])){
                    prop.setAsBoolean(true);
                }else if (value.equals(list[1])){
                    prop.setAsBoolean(false);
                }
            }
        }catch (IndexOutOfBoundsException e) {
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INDEX_OUT_OF_RANGE", mask, prop.getPropetyOwner().getInfo().getType()));
        }catch (RuntimeException e){
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INVALID_STRING_VALUE", prop.getAsObject(), mask));
        }
    }
    
    /**
     * Seta uma String que cont�m um valor em Double como um objeto do tipo Double.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToDouble(IPropertyValue prop, String value) throws PropertyValueException{
        //Retira os separadores de milhar
        String temp = value.replace(".", "");
        // Converte virgula em ponto decimal
        temp = temp.replace(",", ".");
        
        prop.setAsDouble(Double.parseDouble(temp));
    }
    
    /**
     * Seta uma String que cont�m um valor em Float como um objeto do tipo Float.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToFloat(IPropertyValue prop, String value) throws PropertyValueException{
        //Retira os separadores de milhar
        String temp = value.replace(".", "");
        // Converte virgula em ponto decimal
        temp = temp.replace(",", ".");
        
        prop.setAsFloat(Float.parseFloat(temp));
    }
    
    /**
     * Seta uma String que contt�m um valor em Long como um Objeto do tipo Long.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado
     * @throws PropertyValueException
     */
    private static void stringToLong(IPropertyValue prop, String value) throws PropertyValueException{
        //Retira os separadores de milhar
        String temp = value.replace(".", "");
        // Converte virgula em ponto decimal
        temp = temp.replace(",", ".");
        
        prop.setAsLong(Long.parseLong(temp));
    }
    
    private static void stringToInteger(IPropertyValue prop, String value) throws PropertyValueException{
        //Retira os separadores de milhar
        String temp = value.replace(".", "");
        
        prop.setAsInteger(Integer.parseInt(temp));
    }
    
    /**
     * <p>Este m�todo seta uma String que cont�m um valor espec�fico - String, Calendar, BigDecimal, Boolean, Double, Long - 
     * com seus respectivos Tipos.
     * 
     * @param prop
     * @param value - String com o valor a ser transformado e setado conforme seu Tipo.
     * @throws PropertyValueException
     */
    public static void stringToValue(IPropertyValue prop, String value) throws PropertyValueException{
        try{
            
            /* Verifica se o valor � nulo para gravar null
             * E Verifica se � cole��o para limpar a cole��o */
            if (value == null || value.equals("")){
            	if(prop.getPropetyOwner().getInfo().isCollection())
            		if(prop.getPropetyOwner().getInfo().isPrimitive())
            			prop.getAsPrimitiveCollection().clear();
            		else
            			prop.getAsEntityCollection().clear();
            	else
            		prop.setAsObject(null);
            }
            
            else if(prop.getPropetyOwner().getInfo().isString())
                stringToStringValue(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isCalendar())
                stringToCalendar(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isBigDecimal())
                stringToBigDecimal(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isBoolean())
                stringToBoolean(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isDouble())
                stringToDouble(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isFloat())
                stringToFloat(prop, value);
            
            else if(prop.getPropetyOwner().getInfo().isLong())
                stringToLong(prop, value);
            
            else if (prop.getPropetyOwner().getInfo().isInteger())
                stringToInteger(prop, value);
            
        }catch(RuntimeException e){
            throw new PropertyValueException(MessageList.createSingleInternalError(e));
        }
    }
    
    /**
     * <p>Obt�m a String correspondente ao Objeto 
     * 
     * @param prop
     * @return String correspondente
     * @throws PropertyValueException
     */
    private static String stringValueToString(IPropertyValue prop) throws PropertyValueException{
        String mask = prop.getPropetyOwner().getInfo().getEditMask();
    	try{
            if(prop.getPropetyOwner().getInfo().isHasEditMask() && !mask.equals(HTML_EDIT_MASK)){
            	
            	MaskFormatter mf = new MaskFormatter(mask);
            	mf.setValueContainsLiteralCharacters(false);
            	return mf.valueToString(prop.getAsObject().toString());
            }

            return prop.getAsObject().toString();
            
        } catch (ParseException e)
        {
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INVALID_VALUE_STRING", prop.getAsObject(), mask));
        }
    }
    
    /**
     * <p>Obt�m uma String com a data (Calendar) correspondente.
     * <p>Caso a m�scara esteja vazia, uma string vazia ser� retornada
     * pela classe SimpleDateFormat.
     * 
     * @param prop
     * @return uma String contendo a data
     * @throws PropertyValueException
     */
    private static String calendarToString(IPropertyValue prop) throws PropertyValueException{
        return new SimpleDateFormat(prop.getPropetyOwner().getInfo().getEditMask()).format(prop.getAsCalendar().getTime());
    }
    
    /**
     * <p>Obt�m uma String com o valor BigDecimal correspondente.
     * 
     * @param prop
     * @return uma String contendo um valor BigDecimal correspondente.
     * @throws PropertyValueException
     */
    private static String bigDecimalToString(IPropertyValue prop) throws PropertyValueException{
    	/* Converte uma cole��o de BigDecimal numa String separada por `;` */
    	if(prop.getPropetyOwner().getInfo().isCollection()){
        	Collection<BigDecimal> values =  prop.<BigDecimal>getAsPrimitiveCollection();
        	StringBuffer temp = new StringBuffer();
        	for(BigDecimal big: values){
        		if(big != null)
        			temp.append(String.format("%,.2f", big.doubleValue()));
        		temp.append(";");
        	}
        	return temp.toString();
        }else{
        	/* Retira os separadores de milhar */
        	String temp = String.format("%,.2f", prop.getAsBigDecimal().doubleValue()); 
        	/* Converte ponto decimal em virgula  */
//        temp = temp.replace(".", ",");
        	
        	return temp;
        }
        

    }
    
    /**
     * <p>Obt�m um atributo boolean como uma string.
     * <p>Se n�o houver uma m�scara pr�-definida para o atributo, obt�m a lista default,
     * que cont�m [0]=sim e [1]=n�o.
     * <p>Caso tamb�m n�o encontre o valor default, atribui � String o valor padr�o do Java,
     * no caso, true ou false. 
     * 
     * @param prop
     * @return uma String com o valor em Boolean correspondente.
     * @throws PropertyValueException
     */
    private static String booleanToString(IPropertyValue prop) throws PropertyValueException{
        String mask = prop.getPropetyOwner().getInfo().getEditMask();
        try{
            if(StringUtils.isEmpty(mask)){
                return prop.getAsBoolean().toString();
            }

            String[] list = StringUtils.split(mask,",");
            if (prop.getAsBoolean())
                return list[0]; //sim
            
            return list[1]; //n�o
        }catch (IndexOutOfBoundsException e) {
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INDEX_OUT_OF_RANGE", mask, prop.getPropetyOwner().getInfo().getLabel()));
        }catch (RuntimeException e){
            throw new PropertyValueException(MessageList.create(PropertyValueException.class, "INVALID_STRING_VALUE", prop.getAsObject(), mask));
        }
    }
    
    /**
     * <p>Obt�m uma String com o valor Double correspondente.
     * 
     * @param prop
     * @return uma String com o valor Double correspondente.
     * @throws PropertyValueException
     */
    private static String doubleToString(IPropertyValue prop) throws PropertyValueException{
        /* Retira os separadores de milhar */
        String temp = prop.getAsDouble().toString(); 
        /* Converte ponto decimal em virgula  */
        temp = temp.replace(".", ",");

        return temp;
    }
    
    /**
     * <p>Obt�m uma String com o valor Float correspondente.
     * 
     * @param prop
     * @return uma String com o valor Float correspondente.
     * @throws PropertyValueException
     */
    private static String floatToString(IPropertyValue prop) throws PropertyValueException{
        /* Retira os separadores de milhar */
        String temp = prop.getAsFloat().toString(); 
        /* Converte ponto decimal em virgula  */
        temp = temp.replace(".", ",");

        return temp;
    }
    
    /**
     * Obt�m uma String com o valor Long correspondente.
     * 
     * @param prop
     * @return String com valor Long correspondente.
     * @throws PropertyValueException
     */
    private static String longToString(IPropertyValue prop) throws PropertyValueException{
        /* Retira os separadores de milhar */
        String temp = prop.getAsLong().toString(); 
        /* Converte ponto decimal em virgula  */
        temp = temp.replace(".", ",");

        return temp;
    }
    
    private static String integerToString(IPropertyValue prop) throws PropertyValueException{
        return prop.getAsInteger().toString();
    }
    
    /**
     * <p>Este m�todo retorna um valor - String, Calendar, BigDecimal, Boolean, Double, Long - 
     * convertido em String.
     * 
     * @param prop
     * @return String com o valor requerido.
     * @throws PropertyValueException
     */
    public static String valueToString(IPropertyValue prop) throws PropertyValueException{
        // Verifica se o valor � nulo para retornar uma String vazia
        if (prop.getPropetyOwner().getValue().isValueNull())
            return "";
        
        else if(prop.getPropetyOwner().getInfo().isString())
            return stringValueToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isCalendar())
            return calendarToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isBigDecimal())
            return bigDecimalToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isBoolean())
            return booleanToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isDouble())
            return doubleToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isFloat())
            return floatToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isLong())
            return longToString(prop);
        
        else if(prop.getPropetyOwner().getInfo().isInteger())            
            return integerToString(prop);
        
        else
            return prop.getAsObject().toString();
    }
}
