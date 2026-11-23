package WalletManager.controllers;

import WalletManager.dtos.requests.TransactionRequestDTO;
import WalletManager.dtos.response.TransactionResponseDTO;
import WalletManager.mappers.TransactionMapper;
import WalletManager.models.Transaction;
import WalletManager.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions().stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        Transaction savedTransaction = transactionService.createTransaction(dto);
        return new ResponseEntity<>(transactionMapper.toResponse(savedTransaction), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO dto) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, dto);
        return new ResponseEntity<>(transactionMapper.toResponse(updatedTransaction), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>("Transacción eliminada correctamente", HttpStatus.OK);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId) {
        transactionService.importFromCsv(file, accountId);
        return new ResponseEntity<>("Archivo importado y procesado exitosamente", HttpStatus.OK);
    }
}