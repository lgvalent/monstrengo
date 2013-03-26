package br.com.orionsoft.monstrengo.core.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class EnumUtils {

	public static List<SelectItem> enumToSelectItemList(Class<?> arg) {
		Object[] objects = arg.getEnumConstants();
		List<SelectItem> result = new ArrayList<SelectItem>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			result.add(new SelectItem(i, objects[i].toString()));
		}
        return result;
	}
}
