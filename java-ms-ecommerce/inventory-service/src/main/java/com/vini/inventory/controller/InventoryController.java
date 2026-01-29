package com.vini.inventory.controller;

import com.vini.inventory.application.InventoryService;
import com.vini.inventory.dto.ReleaseRequest;
import com.vini.inventory.dto.ReserveRequest;
import com.vini.inventory.dto.ReserveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

  private final InventoryService service;

  @PostMapping("/reserve")
  @ResponseStatus(HttpStatus.OK)
  public ReserveResponse reserve(@RequestBody @Valid ReserveRequest req) {
    service.reserve(req);
    return new ReserveResponse(req.orderId(), "RESERVED");
  }

  @PostMapping("/release")
  @ResponseStatus(HttpStatus.OK)
  public ReserveResponse release(@RequestBody ReleaseRequest req) {
    service.release(req.orderId());
    return new ReserveResponse(req.orderId(), "RELEASED");
  }

  @GetMapping("/stock/{sku}")
  public com.vini.inventory.dto.StockResponse stock(@PathVariable String sku) {
    var s = service.getStock(sku);
    return new com.vini.inventory.dto.StockResponse(s.getSku(), s.getAvailableQuantity());
  }
}
