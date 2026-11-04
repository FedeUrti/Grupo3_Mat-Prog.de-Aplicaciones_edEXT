package com.grupo3_mat.edEXT.Presentacion;

import jakarta.xml.soap.Detail;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.soap.SOAPFaultException;
import javax.xml.namespace.QName;

final class ExcepcionesSoap {
    static final String NAMESPACE = "http://soap.edext.grupo3_mat.com/faults";
    static final String ELEMENTO = "BusinessFault";

    private ExcepcionesSoap() {
    }

    static boolean esExcepcionDeNegocio(Throwable error) {
        return error instanceof IllegalArgumentException
                || error instanceof IllegalStateException
                || error instanceof SecurityException;
    }

    static SOAPFaultException crearFault(Throwable error) {
        try {
            SOAPFault fault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL)
                    .createFault(error.getMessage(),
                            new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client", "soap"));
            Detail detail = fault.addDetail();
            var businessFault = detail.addChildElement(ELEMENTO, "edext", NAMESPACE);
            businessFault.addChildElement("type", "edext", NAMESPACE)
                    .addTextNode(error.getClass().getSimpleName());
            businessFault.addChildElement("message", "edext", NAMESPACE)
                    .addTextNode(error.getMessage() == null ? "" : error.getMessage());
            return new SOAPFaultException(fault);
        } catch (SOAPException soapError) {
            throw new IllegalStateException("No se pudo construir el SOAP Fault de negocio.", soapError);
        }
    }
}
