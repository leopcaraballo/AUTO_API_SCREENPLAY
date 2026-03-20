Feature: Flujo de Atención de Paciente en RLApp-V2
  Como administrador del sistema médico (Supervisor/Doctor/Reception)
  Quiero simular el ciclo de vida de una consulta (Registro -> Llamado -> Finalización)
  Para garantizar que la API CQRS interactúa correctamente con los Modelos de Lectura

  Scenario: Ciclo completo de atención médica
    Given que el personal médico se autentica en el sistema
    When se registra la llegada de un paciente a recepción
    And el administrador médica activa una de las salas
    And el doctor llama al siguiente paciente en la sala
    And el doctor finaliza la consulta médica exitosamente
    Then el paciente debe figurar como atendido en el sistema
