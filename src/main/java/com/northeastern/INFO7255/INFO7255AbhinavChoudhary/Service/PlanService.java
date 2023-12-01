package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Dao.PlanDao;

@Service
public class PlanService {
    
    @Autowired
    PlanDao planDao;

    // @Autowired
    // private MessageQueueService messageQueueService;

    public Map<String, Object> savePlan(String key, JSONObject planObject){
        convertToMap(planObject);
        Map<String, Object> outputMap = new HashMap<String, Object>();
        getOrDeleteData(key, outputMap, false);
        return outputMap;
    }

    public void hashSet(String key, String field, String value ) {
        planDao.hashSet(key, field, value);
    }

    public boolean checkIfKeyExists(String key){
        return planDao.checkIfKeyExist(key);
    }

    public Map<String, Object> getPlan(String key){
        Map<String, Object> outputMap = new HashMap<String, Object>();
        getOrDeleteData(key, outputMap, false);
        return outputMap;
    }

    public String getEtag(String key, String field) {
        return planDao.hashGet(key, field);
    }

    public void deletePlan(String key) {
        getOrDeleteData(key, null, true);
    }

    private Map<String, Map<String, Object>> convertToMap(JSONObject object) {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Map<String, Object> valueMap = new HashMap<String, Object>();

        Iterator<String> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String redisKey = object.get("objectType") + ":" + object.get("objectId") + ":";
            String key = iterator.next();
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                value = convertToMap((JSONObject) value);
                HashMap<String, Map<String, Object>> val = (HashMap<String, Map<String, Object>>) value;
                planDao.addSetValue(redisKey + ":" + key, val.entrySet().iterator().next().getKey());
            } else if (value instanceof JSONArray) {
                value = convertToList((JSONArray) value);
                for (HashMap<String, HashMap<String, Object>> entry : (List<HashMap<String, HashMap<String, Object>>>) value) {
                    for (String listKey : entry.keySet()) {
                        planDao.addSetValue(redisKey + ":" + key, listKey);
                    }
                }
            } else {
                planDao.hashSet(redisKey, key, value.toString());
                valueMap.put(key, value);
                map.put(redisKey, valueMap);
            }

        }
        return map;
    }

    private List<Object> convertToList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = convertToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = convertToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    private Map<String, Object> getOrDeleteData(String redisKey, Map<String, Object> outputMap, boolean isDelete) {
        Set<String> keys = planDao.getKeys(redisKey + "*");
        for (String key : keys) {
            if (key.equals(redisKey)) {
                if (isDelete) {
                    planDao.deleteKeys(new String[] {key});
                } else {
                    Map<String, String> val = planDao.getAllValuesByKey(key);
                    for (String name : val.keySet()) {
                        if (!name.equalsIgnoreCase("eTag")) {
                            outputMap.put(name,
                                    isStringDouble(val.get(name)) ? Double.parseDouble(val.get(name)) : val.get(name));
                        }
                    }
                }

            } else {
                String newStr = key.substring((redisKey + ":").length());
                Set<String> members = planDao.sMembers(key);
                if (members.size() > 1) {
                    List<Object> listObj = new ArrayList<Object>();
                    for (String member : members) {
                        if (isDelete) {
                            getOrDeleteData(member, null, true);
                        } else {
                            Map<String, Object> listMap = new HashMap<String, Object>();
                            listObj.add(getOrDeleteData(member, listMap, false));

                        }
                    }
                    if (isDelete) {
                        planDao.deleteKeys(new String[] {key});
                    } else {
                        outputMap.put(newStr, listObj);
                    }

                } else {
                    if (isDelete) {
                        planDao.deleteKeys(new String[]{members.iterator().next(), key});
                    } else {
                        Map<String, String> val = planDao.getAllValuesByKey(members.iterator().next());
                        Map<String, Object> newMap = new HashMap<String, Object>();
                        for (String name : val.keySet()) {
                            newMap.put(name,
                                    isStringDouble(val.get(name)) ? Double.parseDouble(val.get(name)) : val.get(name));
                        }
                        outputMap.put(newStr, newMap);
                    }
                }
            }
        }
        return outputMap;
    }

    private boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String savePlanToRedis(JSONObject planObject, String key) {
        Map<String, Object> savedPlanMap = savePlan(key, planObject);
        String savedPlan = new JSONObject(savedPlanMap).toString();

        String newEtag = DigestUtils.md5Hex(savedPlan);
        hashSet(key, "eTag", newEtag);
        return newEtag;
    }
}
