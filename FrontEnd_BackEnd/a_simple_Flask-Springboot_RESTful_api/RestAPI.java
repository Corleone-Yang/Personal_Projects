import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
@RequestMapping("/store")
public class StoreController {
    private List<Map<String, Object>> stores = new ArrayList<>();

    public StoreController() {
        Map<String, Object> store = new HashMap<>();
        store.put("name", "My Store");
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("name", "Chair");
        item.put("price", 15.99);
        items.add(item);
        store.put("items", items);
        stores.add(store);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getStores() {
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStore(@RequestBody Map<String, String> request) {
        Map<String, Object> newStore = new HashMap<>();
        newStore.put("name", request.get("name"));
        newStore.put("items", new ArrayList<>());
        stores.add(newStore);
        return new ResponseEntity<>(newStore, HttpStatus.CREATED);
    }

    @PostMapping("/{name}/item")
    public ResponseEntity<?> createItem(@PathVariable String name, @RequestBody Map<String, Object> request) {
        Optional<Map<String, Object>> store = stores.stream()
            .filter(s -> s.get("name").equals(name))
            .findFirst();
        if (store.isPresent()) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) store.get().get("items");
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("name", request.get("name"));
            newItem.put("price", request.get("price"));
            items.add(newItem);
            return new ResponseEntity<>(newItem, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Store not found"));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getStore(@PathVariable String name) {
        return stores.stream()
            .filter(store -> store.get("name").equals(name))
            .findFirst()
            .map(store -> ResponseEntity.ok(store))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Store not found")));
    }

    @GetMapping("/{name}/item")
    public ResponseEntity<?> getItemInStore(@PathVariable String name) {
        Optional<Map<String, Object>> store = stores.stream()
            .filter(s -> s.get("name").equals(name))
            .findFirst();
        if (store.isPresent()) {
            return ResponseEntity.ok(Map.of("items", store.get().get("items")));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Store not found"));
        }
    }
}
