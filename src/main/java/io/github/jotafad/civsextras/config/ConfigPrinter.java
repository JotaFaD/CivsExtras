package io.github.jotafad.civsextras.config;

import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;

import java.io.FileWriter;
import java.io.IOException;

public class ConfigPrinter
{
    private FileWriter writer;

    public ConfigPrinter(FileWriter writer)
    {
        this.writer = writer;
    }

    public void print(YamlMapping mapping) throws IOException
    {
        printMapping(mapping);
        writer.close();
    }

    public void printScalar(Scalar scalar) throws IOException
    {
        if(scalar.value().matches(".*[?#-:>|$%&{}\\[\\]]+.*|[ ]+"))
        {
            writer.write("\"" + scalar.value() + "\"");
        }
        else
        {
            writer.write(scalar.value());
        }
    }

    public void printMapping(YamlMapping mapping) throws IOException
    {
        printMapping(mapping, 0, false);
    }

    public void printMapping(YamlMapping mapping, int indentation) throws IOException
    {
        printMapping(mapping, indentation, false);
    }

    public void printMapping(YamlMapping mapping, int indentation, boolean insideSequence) throws IOException
    {
        for(YamlNode key : mapping.keys())
        {
            if(!insideSequence)
            {
                writer.write(getIndentationString(indentation));
            }
            writer.write(key.asScalar().value());
            writer.write(": ");

            YamlNode value = mapping.value(key);
            if(value instanceof Scalar)
            {
                printScalar((Scalar) value);
                writer.write(getLineSeparator());
            }
            else if(value instanceof YamlMapping)
            {
                writer.write(getLineSeparator());
                printMapping((YamlMapping) value, indentation + 1);
            }
            else if(value instanceof YamlSequence)
            {
                writer.write(getLineSeparator());
                printSequence((YamlSequence) value, indentation + 1);
            }

        }
    }

    public void printSequence(YamlSequence sequence, int indentation) throws IOException
    {
        for(YamlNode value : sequence.values())
        {
            writer.write(getIndentationString(indentation));
            writer.write("- ");
            if(value instanceof Scalar)
            {
                printScalar((Scalar) value);
                writer.write(getLineSeparator());
            }
            else if(value instanceof YamlMapping)
            {
                printMapping((YamlMapping) value, indentation + 1, true);
            }
        }
    }

    private String getIndentationString(int indentation)
    {
        return "  ".repeat(indentation);
    }

    private String getLineSeparator()
    {
        return System.lineSeparator();
    }
}
