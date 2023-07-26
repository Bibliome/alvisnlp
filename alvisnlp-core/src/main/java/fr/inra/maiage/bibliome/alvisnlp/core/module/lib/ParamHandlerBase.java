/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParameterException;

/**
 * Base class for parameter handlers.
 */
public class ParamHandlerBase<A extends Annotable> implements ParamHandler<A> {
    private final ModuleBase<A> owner;
    private final Method getter;
    private final Method setter;
    private final Param annot;
    private final String name;
    private final String nameType;
    private String paramSourceName;
    private boolean outputFeed = false;
    
    /**
     * Instantiates a new param handler base.
     * @param owner
     * @param getter
     * @param annot
     * @throws NoSuchMethodException
     */
    protected ParamHandlerBase(ModuleBase<A> owner, Method getter, Param annot) throws NoSuchMethodException {
        super();
        this.owner = owner;
        this.getter = getter;
        this.annot = annot;
        String getterName = getter.getName();
        String setterName = "set" + getterName.substring(3);
        this.setter = owner.getClass().getMethod(setterName, getter.getReturnType());
        this.name = getterName.substring(3, 4).toLowerCase() + getterName.substring(4);
        this.nameType = annot.nameType().equals("") ? null : annot.nameType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return getter.getReturnType();
    }

    @Override
    public Object getValue() {
        try {
            return getter.invoke(owner);
        }
        catch (IllegalArgumentException e) {
        	throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
        	throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
        	throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMandatory() {
        return annot.mandatory();
    }

    @Override
    public boolean isSet() {
        return getValue() != null;
    }

    @Override
    public void setValue(Object value) throws ParameterException {
        try {
            setter.invoke(owner, value);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOutputFeed() {
		return outputFeed;
	}

    @Override
	public void setOutputFeed(boolean outputFeed) {
		this.outputFeed = outputFeed;
	}
    
    @Override
    public boolean isDeprecated() {
    	Deprecated depr = getter.getAnnotation(Deprecated.class);
    	return depr != null;
    }

	@Override
	public <P> void accept(ModuleVisitor<A,P> visitor, P param) throws ModuleException {
		visitor.visitParam(this, param);
	}

	@Override
	public Module<A> getModule() {
		return owner;
	}

	@Override
	public String getNameType() {
		return nameType;
	}

	@Override
	public String getParamSourceName() {
		if (paramSourceName == null) {
			return owner.getModuleSourceName();
		}
		return paramSourceName;
	}

	@Override
	public void setParamSourceName(String sourceName) {
		paramSourceName = sourceName;
	}
}
