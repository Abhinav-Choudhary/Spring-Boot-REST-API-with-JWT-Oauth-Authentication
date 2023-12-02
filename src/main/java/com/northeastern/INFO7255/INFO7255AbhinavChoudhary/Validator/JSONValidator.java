package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Validator;

import java.io.IOException;
import java.io.InputStream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Service
public class JSONValidator {
    
    public void validateJson(JSONObject object) throws IOException {
            InputStream inputStream = getClass().getResourceAsStream("/JsonSchema.json");
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(object);
    }
}
