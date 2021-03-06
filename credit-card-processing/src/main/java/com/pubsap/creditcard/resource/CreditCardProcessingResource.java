package com.pubsap.creditcard.resource;

import com.pubsap.creditcard.api.CreditCardProcessingApi;
import com.pubsap.creditcard.exceptions.*;
import com.pubsap.creditcard.models.CreditCard;
import com.pubsap.creditcard.service.CreditCardProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
public class CreditCardProcessingResource implements CreditCardProcessingApi {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardProcessingResource.class);

    @Autowired
    private CreditCardProcessingService service;

    @Override
    public ResponseEntity<CreditCard> createCreditCard(CreditCard card, BindingResult bindingResult) {
        LOG.info("Card creation request initiated");
        handleError(bindingResult);
        final CreditCard creditCard = service.createCreditCard(card);
        if (creditCard != null) {
            LOG.info("Credit card successfully created");
            return ResponseEntity.created(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{/id}").buildAndExpand(creditCard.getId()).toUri())
                    .body(creditCard);
        } else return ResponseEntity.badRequest().build();
    }

    @Override
    public List<CreditCard> getAllCreditCards() {
        LOG.info("Fetch all credit cards request initiated");
        List<CreditCard> creditCards = service.getAllCreditCards();
        LOG.info("Fetch all credit cards request completed");
        return creditCards;
    }

    private void handleError(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOG.error("Request has errors");
            if (bindingResult.hasFieldErrors("cardNumber") ) {
                LOG.error("Credit card Number is invalid");
                throw new InvalidCreditCardException(HttpStatus
                        .BAD_REQUEST.value(), ErrorMessages.INVALID_CREDIT_CARD.getErrorMsg());
            } else if (bindingResult.hasFieldErrors("name")) {
                LOG.error("Invalid user name");
                throw new InvalidUserNameException(HttpStatus
                        .BAD_REQUEST.value(), ErrorMessages.INVALID_USER_NAME.getErrorMsg());
            } else if (bindingResult.hasFieldErrors("creditLimit")) {
                LOG.error("Invalid credit limit");
                throw new InvalidCreditLimitException(HttpStatus
                        .BAD_REQUEST.value(), ErrorMessages.INVALID_CREDITLIMIT.getErrorMsg());
            }
            else if (bindingResult.hasFieldErrors("balance")) {
                LOG.error("Invalid balance");
                throw new InvalidBalanceException(HttpStatus
                        .BAD_REQUEST.value(), ErrorMessages.INVALID_BALANCE.getErrorMsg());
            }
        }
    }
}
