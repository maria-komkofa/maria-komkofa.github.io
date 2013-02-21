package samara;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;

/**
 * Класс для генерации XML инстанса (т.е. корректной XML) по XSD схемам.
 */
public class XSDs2XMLGenerator {

    /**
     * Метод для генерации XML инстанса по XSD схемам.
     * 
     * @param schemaFiles
     *                массив файлов схем
     * @param rootName
     *                рутовый тип - откуда будет старт разворота всех подтипов
     * @return XML в виде строки или null если ошибки
     */
    @SuppressWarnings("unchecked")
    private static String generate(File[] schemaFiles, String rootName) {
		// Process Schema files
		List sdocs = new ArrayList();
		for (File schemaFile : schemaFiles) {
		    try {
			sdocs.add(XmlObject.Factory.parse(schemaFile, (new XmlOptions()).setLoadLineNumbers()
				.setLoadMessageDigest()));
		    } catch (Exception e) {
			System.out.println("Can not load schema file: " + schemaFile + ": ");
			return null;
		    }
		}

		XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

		SchemaTypeSystem sts = null;
		if (schemas.length > 0) {
		    XmlOptions compileOptions = new XmlOptions();
		    try {
			sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
		    } catch (Exception e) {
			System.out.println("Compilation error!");
			return null;
		    }
		}

		if (sts == null) {
		    System.out.println("No Schemas to process.");
		    return null;
		}
		SchemaType[] globalTypes = sts.globalTypes();
		SchemaType elem = null;
		for (SchemaType globalType : globalTypes) {
			    if (rootName.equals(globalType.getName().getLocalPart())) {
				elem = globalType;
				break;
		    }
		}

		if (elem == null) {
		    System.out.println("Could not find a global element with name \"" + rootName + "\"");
		    return null;
		}

		// Now generate it
		return SampleXmlUtil.createSampleForType(elem);
    }

    /**
     * Получает список файлов из указанной директории подходящих под заданный фильтр.
     * 
     * @param targetDir
     *                указанная директория
     * @param fileNameFilter
     *                заданный фильтр
     * @return список файлов
     */
    private static File[] getAllFilesInDir(String targetDir, final String fileNameFilter) {
		FilenameFilter filter = new FilenameFilter() {

	    @Override
	    public boolean accept(File dir, String name) {
			return name.contains(fileNameFilter);
	    }
	};
		return new File(targetDir).listFiles(filter);
    }

    private XSDs2XMLGenerator() {
    }

    public static void main(String[] args) {
		System.out.println(generate(getAllFilesInDir(XSDs2XMLGenerator.class.getResource("../xsd").getFile(), ".xsd"), "FindGibddRequestType"));
    }1
}
