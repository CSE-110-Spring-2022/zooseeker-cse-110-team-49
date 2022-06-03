package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;



public class ZooDataItem {

    public static class VertexInfo {
        public static enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION
        }

        public String id;
        public Kind kind;
        public String name;
        public List<String> tags;
    }

    public static class EdgeInfo {
        public String id;
        public String street;
    }

    public static Map<String, ZooDataItem.VertexInfo> loadVertexInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);

            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<VertexInfo>>() {
            }.getType();
            List<VertexInfo> zooData = gson.fromJson(reader, type);

            Map<String, VertexInfo> indexedZooData = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            reader.close();
            return indexedZooData;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Map<String, ZooDataItem> loadZooItemInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);

            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooDataItem>>() {
            }.getType();

            List<ZooDataItem> zooData = gson.fromJson(reader, type);

            Map<String, ZooDataItem> indexedZooData = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            reader.close();
            return indexedZooData;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Map<String, ZooDataItem.EdgeInfo> loadEdgeInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);

            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooDataItem.EdgeInfo>>() {
            }.getType();
            List<ZooDataItem.EdgeInfo> zooData = gson.fromJson(reader, type);

            Map<String, ZooDataItem.EdgeInfo> indexedZooData = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            return indexedZooData;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }


    public static Map<String, Trail> loadTrailJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);

            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<Trail>>() {
            }.getType();
            List<Trail> zooData = gson.fromJson(reader, type);

            Map<String, Trail> indexedZooData = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            return indexedZooData;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Graph<String, IdentifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);


            JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

            importer.setVertexFactory(v -> v);

            importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);


            Reader reader = new InputStreamReader(inputStream);

            importer.importGraph(g, reader);

            return g;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }

    /**
     * Load ZooNode's from a JSON file (such as vertex_info.json).
     *
     * @param infoReader a reader from which to read the JSON input.
     * @return a list
     */
    public static List<Exhibit> fromJson(Reader infoReader) {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>() {
        }.getType();
        return gson.fromJson(infoReader, type);
    }

    public static void toJson(List<Exhibit> infos, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>() {
        }.getType();
        gson.toJson(infos, type, writer);
        writer.flush();
        writer.close();
    }

    public enum Kind {
        // The SerializedName annotation tells GSON how to convert
        // from the strings in our JSON to this Enum.
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION,
        @SerializedName("exhibit_group") EXHIBIT_GROUP;
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @NonNull
    public final String id;

    @ColumnInfo(name = "group_id")
    @SerializedName("group_id")
    @Nullable
    public final String groupId;

    @ColumnInfo(name = "kind")
    @SerializedName("kind")
    @NonNull
    public final Kind kind;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "tags")
    @SerializedName("tags")
    @NonNull
    public final List<String> tags;

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    public final Double lat;

    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    public final Double lng;

    public boolean isExhibit() {
        return kind.equals(Kind.EXHIBIT);
    }

    public boolean isIntersection() {
        return kind.equals(Kind.INTERSECTION);
    }

    public boolean isGroup() {
        return kind.equals(Kind.EXHIBIT_GROUP);
    }

    public boolean hasGroup() {
        return groupId != null;
    }

    public ZooLocation getLocation() {
        return new ZooLocation(this.lat, this.lng);
    }

    public ZooDataItem(@NonNull String id,
                        @Nullable String groupId,
                        @NonNull Kind kind,
                        @NonNull String name,
                        @NonNull List<String> tags,
                        @Nullable Double lat,
                        @Nullable Double lng) {
        this.id = id;
        this.groupId = groupId;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;

        if (!this.hasGroup() && (lat == null || lng == null)) {
            throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
        }
    }

}