export class ValdiatorError extends Error {
  constructor(message: string) {
    super(message);

    // Set the prototype explicitly.
    Object.setPrototypeOf(this, ValdiatorError.prototype);
  }
}
