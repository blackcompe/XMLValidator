import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLValidator
{

    private URL schemaURL;
    private Validator validator;

    public XMLValidator()
    {
        this(null);
    }

    public XMLValidator(String schemaURL)
    {
        try
        {
            this.schemaURL = (schemaURL == null) ? null : new URL(schemaURL);
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = (schemaURL == null) ? factory.newSchema() : factory
                    .newSchema(this.schemaURL);
            validator = schema.newValidator();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isValid(URL url) throws IOException, SAXException
    {
        return isValid(new StreamSource(url.openStream()));
    }

    public boolean isValid(String file) throws IOException, SAXException
    {
        return isValid(new StreamSource(new BufferedInputStream(
                new FileInputStream(file))));
    }

    private boolean isValid(Source src) throws IOException
    {
        ValidatingHandler errorHandler = new ValidatingHandler();
        validator.setErrorHandler(errorHandler);
        try
        {
            validator.validate(src);
        } catch (SAXException e)
        {
            return false;
        }
        return errorHandler.errorOccured();
    }

    private class ValidatingHandler implements ErrorHandler
    {

        private boolean parseError;

        @Override
        public void error(SAXParseException e)
        {
            parseError = true;
        }

        @Override
        public void fatalError(SAXParseException e)
        {
            parseError = true;
        }

        @Override
        public void warning(SAXParseException exception)
        {
        }

        public boolean errorOccured()
        {
            return parseError;
        }
    }

    public static void main(String[] args) throws Exception
    {
        String url = "";
        XMLValidator validator = new XMLValidator();
        System.out.println("Is Valid XML = " + validator.isValid(new URL(url)));
    }

}
