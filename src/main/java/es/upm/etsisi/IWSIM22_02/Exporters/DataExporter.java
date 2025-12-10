package es.upm.etsisi.IWSIM22_02.Exporters;

import java.sql.ResultSet;

public interface DataExporter {
    void export(ResultSet resultSet) throws Exception;
}

//Para estos dos tipos de Exporters realmente no hace falta una interfaz pero bueno
//imaginé que habría sufuciente en común como para que tuviese sentido hacer una interfaz,
//sobre to_do de cara a que pudiese haber más tipos de export a futuro (.txt, por ejemplo)