package quizzer.fivestack.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Home", description = "Health check endpoint")
public class HomeController {

    @ResponseBody
    @Operation(summary = "Health check", description = "Returns a simple message to confirm the application is running")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is running")
    })
    @GetMapping("/")
    public String home() {
        return "App is running";
    }

}
