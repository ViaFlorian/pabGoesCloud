import { OperatorEnum } from '../enum/operator.enum';

export function filterMitOperator(operatorEnum: OperatorEnum, datenWert: number, filterWert: number): boolean {
  switch (operatorEnum) {
    case OperatorEnum.EQUALS:
      return datenWert === filterWert;
    case OperatorEnum.GREATER_THAN:
      return datenWert > filterWert;
    case OperatorEnum.GREATER_EQUALS:
      return datenWert >= filterWert;
    case OperatorEnum.SMALLER_THAN:
      return datenWert < filterWert;
    case OperatorEnum.SMALLER_EQUALS:
      return datenWert <= filterWert;
    default:
      return false;
  }
}
