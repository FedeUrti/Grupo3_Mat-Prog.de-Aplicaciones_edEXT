package com.grupo3_mat.edEXT.Presentacion;

import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.spi.Invoker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MapeadorExcepcionesSoap extends Invoker {
    private final Object implementacion;

    MapeadorExcepcionesSoap(Object implementacion) {
        this.implementacion = implementacion;
    }

    @Override
    public void inject(WebServiceContext context) {
    }

    @Override
    public Object invoke(Method method, Object... arguments) throws InvocationTargetException {
        try {
            return method.invoke(implementacion, arguments);
        } catch (InvocationTargetException error) {
            Throwable causa = error.getCause();
            if (ExcepcionesSoap.esExcepcionDeNegocio(causa)) {
                throw new InvocationTargetException(ExcepcionesSoap.crearFault(causa));
            }
            throw error;
        } catch (IllegalAccessException | IllegalArgumentException error) {
            throw new InvocationTargetException(error);
        }
    }
}
