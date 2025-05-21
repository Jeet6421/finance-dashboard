package com.financedash.finance_dashboard.registration;

import com.financedash.finance_dashboard.config.ApiEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.AuthPaths.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Handles user registration and email confirmation")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Confirm user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @GetMapping(ApiEndpoints.AuthPaths.CONFIRM)
    public RegistrationResponse confirmEmail(@RequestParam("token") @NotBlank String token) {
        String result = registrationService.confirmToken(token);
        return RegistrationResponse.success(result);
    }
}
