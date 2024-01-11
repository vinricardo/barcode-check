package barcode.check.controller;

import barcode.check.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/barcode")
public class BarcodeController {
    private final BarcodeService service;

    @GetMapping("/{barcode}")
    public void find(@PathVariable("barcode") String barcode) {
        service.checkBarcode(barcode);
    }
}
