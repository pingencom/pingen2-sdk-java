package com.pingen.sdk.integration;

import ch.codeblock.qrinvoice.documents.DocumentTemplateRepository;
import ch.codeblock.qrinvoice.documents.QrInvoiceDocumentCreator;
import ch.codeblock.qrinvoice.documents.model.application.builder.DocumentLayoutBuilder;
import ch.codeblock.qrinvoice.documents.model.application.builder.InvoiceDocumentBuilder;
import ch.codeblock.qrinvoice.documents.model.application.InvoiceDocument;
import ch.codeblock.qrinvoice.documents.model.application.layout.DocumentLayout;
import ch.codeblock.qrinvoice.model.QrInvoice;
import ch.codeblock.qrinvoice.model.alternativeschemes.ebill.builder.EBillBuilder;
import ch.codeblock.qrinvoice.model.builder.QrInvoiceBuilder;
import ch.codeblock.qrinvoice.util.QRReferenceUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

class TestDocumentCreator {

    static final String INVOICE_NUMBER = "INT-TEST-001";
    static final String CREDITOR_NAME = "Codeblock GmbH";
    static final String DEBTOR_NAME = "Claude Gex";
    static final BigDecimal INVOICE_AMOUNT = new BigDecimal("99.90");
    static final String FILE_NAME = "JavaSdk-Invoice_TEST.pdf";
    static final String FILE_NAME_CANCELLABLE = "JavaSdk-Invoice_TEST_simulate_cancellable.pdf";

    private static final String CREDITOR_IBAN = "CH6030000001893035668";

    static byte[] createInvoicePdf() {
        try {
            QrInvoice qrInvoice = QrInvoiceBuilder.create()
                    .creditorIBAN(CREDITOR_IBAN)
                    .creditor(c -> c.structuredAddress()
                            .name(CREDITOR_NAME)
                            .streetName("Lindenrain")
                            .houseNumber("2")
                            .postalCode("3012")
                            .city("Bern")
                            .country("CH"))
                    .ultimateDebtor(d -> d.structuredAddress()
                            .name(DEBTOR_NAME)
                            .streetName("Musterstrasse")
                            .houseNumber("1")
                            .postalCode("8000")
                            .city("Zürich")
                            .country("CH"))
                    .paymentAmountInformation(p -> p.chf(INVOICE_AMOUNT))
                    .paymentReference(r -> r.qrReference(QRReferenceUtils.createQrReference("42")))
                    .alternativeSchemeParameters(List.of(EBillBuilder.create().bill().billRecipientId("41100000014283293").build().toAlternativeSchemeParameterString()))
                    .build();

            InvoiceDocument invoice = InvoiceDocumentBuilder.create()
                    .invoiceNr(INVOICE_NUMBER)
                    .invoiceDate(LocalDate.now())
                    .invoiceDueDate(LocalDate.now().plusDays(30))
                    .sender(s -> s
                            .addAddressLine(CREDITOR_NAME)
                            .addAddressLine("Lindenrain 2")
                            .addAddressLine("3012 Bern"))
                    .recipient(r -> r
                            .addAddressLine(DEBTOR_NAME)
                            .addAddressLine("Musterstrasse 1")
                            .addAddressLine("8000 Zürich"))
                    .addPosition(p -> p
                            .position(1)
                            .description("Pingen SDK Integration Test Service")
                            .quantity(1)
                            .unitPriceVatInclusive(99.90)
                            .vatPercentage(0.0))
                    .termOfPaymentDays(30)
                    .currency(Currency.getInstance("CHF"))
                    .prefaceText("We allow you to invoice us for the following services:")
                    .endingText("Thank you for your business! Contact us if you have any questions.")
                    .qrInvoice(qrInvoice)
                    .build()
                    .calculate();

            DocumentLayout layout = DocumentLayoutBuilder.create()
                    .documentTemplate(DocumentTemplateRepository.STANDARD_RECIPIENT_RIGHT_V1)
                    .build();

            return QrInvoiceDocumentCreator.create()
                    .invoice(invoice)
                    .invoiceLayout(layout)
                    .inGerman()
                    .withBoundaryLines()
                    .withScissors()
                    .createQrInvoiceDocument()
                    .getData();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create invoice PDF", e);
        }
    }
}
