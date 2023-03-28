package com.mytaxi.plugins;

import com.freenow.apis.phrase.api.format.Format;
import com.freenow.apis.phrase.api.format.JavaPropertiesFormat;
import java.util.HashMap;
import java.util.Map;

public class FileFormat
{
    private String name = "properties";
    private Map<String, String> options = new HashMap<>();


    Format toFormat()
    {

        Format format;

        if (name.equals(JavaPropertiesFormat.newBuilder().build().getName()))
        {
            JavaPropertiesFormat.Builder builder = JavaPropertiesFormat.newBuilder();

            for (Map.Entry<String, String> option : options.entrySet())
            {
                builder.setOption(option.getKey(), option.getValue());
            }

            format = builder.build();
        }
        else
        {
            throw new IllegalStateException("unsupported format: " + name);
        }

        return format;
    }


    @Override
    public String toString()
    {
        return "FileFormat{" +
            "name='" + name + '\'' +
            ", options=" + options +
            '}';
    }
}
