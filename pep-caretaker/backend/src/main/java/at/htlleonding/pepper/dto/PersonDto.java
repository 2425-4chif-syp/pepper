package at.htlleonding.pepper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonDto(
        Long id,
        String firstName,
        String lastName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dob,
        String roomNo,
        Boolean isWorker,
        Boolean gender
) {}
