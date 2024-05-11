package org.example.fairnessawareloaddistribution.config;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author ankushs
 */
@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
@ToString
public class FairnessConfig {

    private final int numberOfReplicas = 5;
    private final SortedMap<Long, String> circle = new TreeMap<>();
    private final Map<String, Set<String>> nodeToKeys = new HashMap<>() {
        @Override
        public Set<String> get(Object key) {
            return super.getOrDefault(key, new HashSet<>());
        }
    };

    public synchronized void addNode(String node) {
        IntStream.range(0, numberOfReplicas).mapToLong(i -> hash(node + "-" + i)).forEach(hash -> circle.put(hash, node));
        redistributeKeys();
    }

    public synchronized void removeNode(String node) {
        IntStream.range(0, numberOfReplicas).mapToLong(i -> hash(node + "-" + i)).forEach(circle::remove);
        Set<String> removedNodeKeys = nodeToKeys.get(node);
        nodeToKeys.remove(node);
        removedNodeKeys.forEach(this::addKey);
    }

    private void redistributeKeys() {
        Map<String, Set<String>> newNodeToKeys = new HashMap<>();
        nodeToKeys.forEach((key1, value) -> value.forEach(key -> {
            String node = getNode(key);
            newNodeToKeys.computeIfAbsent(node, k -> new HashSet<>()).add(key);
        }));
        nodeToKeys.clear();
        nodeToKeys.putAll(newNodeToKeys);
    }

    /**
     * TODO optimize to only add delta key & remove old keys
     * */
    public synchronized void setKeys(Set<String> keys) {
        nodeToKeys.clear();
        keys.forEach(this::addKey);
    }

    private void addKey(String key) {
        String node = getNode(key);
        Set<String> keys = nodeToKeys.get(node);
        keys.add(key);
        nodeToKeys.put(node, keys);
    }

    public synchronized String getNode(String key) {
        if (circle.isEmpty()) {
            throw new IllegalStateException("Circle is empty");
        }
        long hash = hash(key);
        NavigableMap<Long, String> navigableCircle = (NavigableMap<Long, String>) circle;
        Long nodeHash = navigableCircle.ceilingKey(hash);
        if (nodeHash == null) {
            nodeHash = circle.firstKey();
        }
        return circle.get(nodeHash);
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(key.getBytes());
            byte[] digest = md.digest();
            return ByteBuffer.wrap(digest).getLong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing key: " + key, e);
        }
    }

    public synchronized Set<String> getKeysForNode(String node) {
        return nodeToKeys.get(node);
    }

    public static void main(String[] args) {
        FairnessConfig config = new FairnessConfig();
        config.addNode("node1");
        config.addNode("node2");
        config.addNode("node3");

        config.setKeys(Set.of("key1", "key2", "key3", "key4", "key5", "key6", "key7", "key8", "key9", "key10", "key11"));

        System.out.println(config.getKeysForNode("node1"));
        System.out.println(config.getKeysForNode("node2"));
        System.out.println(config.getKeysForNode("node3"));

        config.removeNode("node1");

        System.out.println(config.getKeysForNode("node2"));
        System.out.println(config.getKeysForNode("node3"));
    }
}
