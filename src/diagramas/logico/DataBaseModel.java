/*
 * Copyright (C) 2017 chcan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package diagramas.logico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author chcan
 */
public class DataBaseModel implements Serializable {

    private static final long serialVersionUID = 2017041613233765643L;
    
    public DataBaseModel(){
        InicieAnsi2011();
    }
    
    private ArrayList<String> reservedWords = new ArrayList<>();
    //private ArrayList<String> dataTypes = new ArrayList<>();
    private String nome = "";
    private String versao = "";
    private String descricao = "";
    private HashMap<String, String> dataTypesToJava = new HashMap<>();

    public HashMap<String, String> getDataTypesToJava() {
        return dataTypesToJava;
    }

    public void setDataTypesToJava(HashMap<String, String> dataTypesToJava) {
        this.dataTypesToJava = dataTypesToJava;
    }

    public ArrayList<String> getReservedWords() {
        return reservedWords;
    }

    public void setReservedWords(ArrayList<String> reservedWords) {
        this.reservedWords = reservedWords;
    }

    List<String> dataTypes = null;
    
    public List<String> getDataTypes() {
        if (dataTypes == null) {
            dataTypes = dataTypesToJava.keySet().stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
        }
        return dataTypes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    private final void InicieAnsi2011() {
        setNome("SQL Ansi 2011 - w3c");
        setVersao("1.0.0");
        setDescricao("Copiado de sites na internet - 15/04/2017");
        List<String> lst = getReservedWords();
        lst.clear();
        lst.add("ABS");
        lst.add("ALL");
        lst.add("ALLOCATE");
        lst.add("ALTER");
        lst.add("AND");
        lst.add("ANY");
        lst.add("ARE");
        lst.add("ARRAY");
        lst.add("ARRAY_AGG");
        lst.add("AS");
        lst.add("ASENSITIVE");
        lst.add("ASYMMETRIC");
        lst.add("AT");
        lst.add("ATOMIC");
        lst.add("AUTHORIZATION");
        lst.add("AVG");
        lst.add("BEGIN");
        lst.add("BETWEEN");
        lst.add("BIGINT");
        lst.add("BINARY");
        lst.add("BLOB");
        lst.add("BOOLEAN");
        lst.add("BOTH");
        lst.add("BY");
        lst.add("CALL");
        lst.add("CALLED");
        lst.add("CARDINALITY");
        lst.add("CASCADED");
        lst.add("CASE");
        lst.add("CAST");
        lst.add("CEIL");
        lst.add("CEILING");
        lst.add("CHAR");
        lst.add("CHAR_LENGTH");
        lst.add("CHARACTER");
        lst.add("CHARACTER_LENGTH");
        lst.add("CHECK");
        lst.add("CLOB");
        lst.add("CLOSE");
        lst.add("COALESCE");
        lst.add("COLLATE");
        lst.add("COLLECT");
        lst.add("COLUMN");
        lst.add("COMMIT");
        lst.add("CONDITION");
        lst.add("CONNECT");
        lst.add("CONSTRAINT");
        lst.add("CONVERT");
        lst.add("CORR");
        lst.add("CORRESPONDING");
        lst.add("COUNT");
        lst.add("COVAR_POP");
        lst.add("COVAR_SAMP");
        lst.add("CREATE");
        lst.add("CROSS");
        lst.add("CUBE");
        lst.add("CUME_DIST");
        lst.add("CURRENT");
        lst.add("CURRENT_CATALOG");
        lst.add("CURRENT_DATE");
        lst.add("CURRENT_DEFAULT_TRANSFORM_GROUP");
        lst.add("CURRENT_PATH");
        lst.add("CURRENT_ROLE");
        lst.add("CURRENT_SCHEMA");
        lst.add("CURRENT_TIME");
        lst.add("CURRENT_TIMESTAMP");
        lst.add("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
        lst.add("CURRENT_USER");
        lst.add("CURSOR");
        lst.add("CYCLE");
        lst.add("DATALINK");
        lst.add("DATE");
        lst.add("DAY");
        lst.add("DEALLOCATE");
        lst.add("DEC");
        lst.add("DECIMAL");
        lst.add("DECLARE");
        lst.add("DEFAULT");
        lst.add("DELETE");
        lst.add("DENSE_RANK");
        lst.add("DEREF");
        lst.add("DESCRIBE");
        lst.add("DETERMINISTIC");
        lst.add("DISCONNECT");
        lst.add("DISTINCT");
        lst.add("DLNEWCOPY");
        lst.add("DLPREVIOUSCOPY");
        lst.add("DLURLCOMPLETE");
        lst.add("DLURLCOMPLETEONLY");
        lst.add("DLURLCOMPLETEWRITE");
        lst.add("DLURLPATH");
        lst.add("DLURLPATHONLY");
        lst.add("DLURLPATHWRITE");
        lst.add("DLURLSCHEME");
        lst.add("DLURLSERVER");
        lst.add("DLVALUE");
        lst.add("DOUBLE");
        lst.add("DROP");
        lst.add("DYNAMIC");
        lst.add("EACH");
        lst.add("ELEMENT");
        lst.add("ELSE");
        lst.add("END");
        lst.add("END-EXEC");
        lst.add("ESCAPE");
        lst.add("EVERY");
        lst.add("EXCEPT");
        lst.add("EXEC");
        lst.add("EXECUTE");
        lst.add("EXISTS");
        lst.add("EXP");
        lst.add("EXTERNAL");
        lst.add("EXTRACT");
        lst.add("FALSE");
        lst.add("FETCH");
        lst.add("FILTER");
        lst.add("FIRST_VALUE");
        lst.add("FLOAT");
        lst.add("FLOOR");
        lst.add("FOR");
        lst.add("FOREIGN KEY"); // POR QUESTÃO DE FORMATAÇÃO  - INCLUÍDO POR CHC
        lst.add("FOREIGN");
        lst.add("FREE");
        lst.add("FROM");
        lst.add("FULL");
        lst.add("FUNCTION");
        lst.add("FUSION");
        lst.add("GET");
        lst.add("GLOBAL");
        lst.add("GRANT");
        lst.add("GROUP");
        lst.add("GROUPING");
        lst.add("HAVING");
        lst.add("HOLD");
        lst.add("HOUR");
        lst.add("IDENTITY");
        lst.add("IMPORT");
        lst.add("IN");
        lst.add("INDICATOR");
        lst.add("INNER");
        lst.add("INOUT");
        lst.add("INSENSITIVE");
        lst.add("INSERT");
        lst.add("INT");
        lst.add("INTEGER");
        lst.add("INTERSECT");
        lst.add("INTERSECTION");
        lst.add("INTERVAL");
        lst.add("INTO");
        lst.add("IS");
        lst.add("JOIN");
        lst.add("LAG");
        lst.add("LANGUAGE");
        lst.add("LARGE");
        lst.add("LAST_VALUE");
        lst.add("LATERAL");
        lst.add("LEAD");
        lst.add("LEADING");
        lst.add("LEFT");
        lst.add("LIKE");
        lst.add("LIKE_REGEX");
        lst.add("LN");
        lst.add("LOCAL");
        lst.add("LOCALTIME");
        lst.add("LOCALTIMESTAMP");
        lst.add("LOWER");
        lst.add("MATCH");
        lst.add("MAX");
        lst.add("MAX_CARDINALITY");
        lst.add("MEMBER");
        lst.add("MERGE");
        lst.add("METHOD");
        lst.add("MIN");
        lst.add("MINUTE");
        lst.add("MOD");
        lst.add("MODIFIES");
        lst.add("MODULE");
        lst.add("MONTH");
        lst.add("MULTISET");
        lst.add("NATIONAL");
        lst.add("NATURAL");
        lst.add("NCHAR");
        lst.add("NCLOB");
        lst.add("NEW");
        lst.add("NO");
        lst.add("NONE");
        lst.add("NORMALIZE");
        lst.add("NOT");
        lst.add("NTH_VALUE");
        lst.add("NTILE");
        lst.add("NULL");
        lst.add("NULLIF");
        lst.add("NUMERIC");
        lst.add("OCCURRENCES_REGEX");
        lst.add("OCTET_LENGTH");
        lst.add("OF");
        lst.add("OFFSET");
        lst.add("OLD");
        lst.add("ON");
        lst.add("ONLY");
        lst.add("OPEN");
        lst.add("OR");
        lst.add("ORDER");
        lst.add("OUT");
        lst.add("OUTER");
        lst.add("OVER");
        lst.add("OVERLAPS");
        lst.add("OVERLAY");
        lst.add("PARAMETER");
        lst.add("PARTITION");
        lst.add("PERCENT_RANK");
        lst.add("PERCENTILE_CONT");
        lst.add("PERCENTILE_DISC");
        lst.add("POSITION");
        lst.add("POSITION_REGEX");
        lst.add("POWER");
        lst.add("PRECISION");
        lst.add("PREPARE");
        lst.add("PRIMARY KEY"); // POR QUESTÃO DE FORMATAÇÃO  - INCLUÍDO POR CHC
        lst.add("PRIMARY");
        lst.add("PROCEDURE");
        lst.add("RANGE");
        lst.add("RANK");
        lst.add("READS");
        lst.add("REAL");
        lst.add("RECURSIVE");
        lst.add("REF");
        lst.add("REFERENCES");
        lst.add("REFERENCING");
        lst.add("REGR_AVGX");
        lst.add("REGR_AVGY");
        lst.add("REGR_COUNT");
        lst.add("REGR_INTERCEPT");
        lst.add("REGR_R2");
        lst.add("REGR_SLOPE");
        lst.add("REGR_SXX");
        lst.add("REGR_SXY");
        lst.add("REGR_SYY");
        lst.add("RELEASE");
        lst.add("RESULT");
        lst.add("RETURN");
        lst.add("RETURNS");
        lst.add("REVOKE");
        lst.add("RIGHT");
        lst.add("ROLLBACK");
        lst.add("ROLLUP");
        lst.add("ROW");
        lst.add("ROW_NUMBER");
        lst.add("ROWS");
        lst.add("SAVEPOINT");
        lst.add("SCOPE");
        lst.add("SCROLL");
        lst.add("SEARCH");
        lst.add("SECOND");
        lst.add("SELECT");
        lst.add("SENSITIVE");
        lst.add("SESSION_USER");
        lst.add("SET");
        lst.add("SIMILAR");
        lst.add("SMALLINT");
        lst.add("SOME");
        lst.add("SPECIFIC");
        lst.add("SPECIFICTYPE");
        lst.add("SQL");
        lst.add("SQLEXCEPTION");
        lst.add("SQLSTATE");
        lst.add("SQLWARNING");
        lst.add("SQRT");
        lst.add("START");
        lst.add("STATIC");
        lst.add("STDDEV_POP");
        lst.add("STDDEV_SAMP");
        lst.add("SUBMULTISET");
        lst.add("SUBSTRING");
        lst.add("SUBSTRING_REGEX");
        lst.add("SUM");
        lst.add("SYMMETRIC");
        lst.add("SYSTEM");
        lst.add("SYSTEM_USER");
        lst.add("TABLE");
        lst.add("TABLESAMPLE");
        lst.add("THEN");
        lst.add("TIME");
        lst.add("TIMESTAMP");
        lst.add("TIMEZONE_HOUR");
        lst.add("TIMEZONE_MINUTE");
        lst.add("TO");
        lst.add("TRAILING");
        lst.add("TRANSLATE");
        lst.add("TRANSLATE_REGEX");
        lst.add("TRANSLATION");
        lst.add("TREAT");
        lst.add("TRIGGER");
        lst.add("TRIM");
        lst.add("TRIM_ARRAY");
        lst.add("TRUE");
        lst.add("TRUNCATE");
        lst.add("UESCAPE");
        lst.add("UNION");
        lst.add("UNIQUE");
        lst.add("UNKNOWN");
        lst.add("UNNEST");
        lst.add("UPDATE");
        lst.add("UPPER");
        lst.add("USER");
        lst.add("USING");
        lst.add("VALUE");
        lst.add("VALUES");
        lst.add("VAR_POP");
        lst.add("VAR_SAMP");
        lst.add("VARBINARY");
        lst.add("VARCHAR");
        lst.add("VARYING");
        lst.add("WHEN");
        lst.add("WHENEVER");
        lst.add("WHERE");
        lst.add("WIDTH_BUCKET");
        lst.add("WINDOW");
        lst.add("WITH");
        lst.add("WITHIN");
        lst.add("WITHOUT");
        lst.add("XML");
        lst.add("XMLAGG");
        lst.add("XMLATTRIBUTES");
        lst.add("XMLBINARY");
        lst.add("XMLCAST");
        lst.add("XMLCOMMENT");
        lst.add("XMLCONCAT");
        lst.add("XMLDOCUMENT");
        lst.add("XMLELEMENT");
        lst.add("XMLEXISTS");
        lst.add("XMLFOREST");
        lst.add("XMLITERATE");
        lst.add("XMLNAMESPACES");
        lst.add("XMLPARSE");
        lst.add("XMLPI");
        lst.add("XMLQUERY");
        lst.add("XMLSERIALIZE");
        lst.add("XMLTABLE");
        lst.add("XMLTEXT");
        lst.add("XMLVALIDATE");
        lst.add("YEAR");
        
        dataTypes = null;
        
        dataTypesToJava.clear();
        dataTypesToJava.put("BIT", "short");
        dataTypesToJava.put("BLOB", "byte[]");
        dataTypesToJava.put("BOOLEAN", "boolean");
        dataTypesToJava.put("CHAR", "char");
        dataTypesToJava.put("CHARACTER", "String");
        dataTypesToJava.put("CLOB", "byte");
        dataTypesToJava.put("DATE", "Date");
        dataTypesToJava.put("DECIMAL", "double");
        dataTypesToJava.put("DOUBLE", "double");
        dataTypesToJava.put("FLOAT", "float");
        dataTypesToJava.put("INTEGER", "int");
        dataTypesToJava.put("NCHAR", "String");
        dataTypesToJava.put("NUMERIC", "int");
        dataTypesToJava.put("REAL", "double");
        dataTypesToJava.put("SMALLINT", "short");
        dataTypesToJava.put("TIME", "Date");
        dataTypesToJava.put("TIMESTAMP", "Date");
        dataTypesToJava.put("VARCHAR", "String");
        dataTypesToJava.put("XML", "org.w3c.dom.Document");
    }
    
 
    //??: TO-DO: FUTURO: Carregar arquivos de outras implementações para o modelo físico.
    //??: TO-DO: FUTURO: No modelo lógico: colocar a opção de troca e exibí-lo no inspector.
}
