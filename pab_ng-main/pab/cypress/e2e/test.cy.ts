describe('template spec', () => {
  beforeEach(() => {
    cy.viewport(1680, 1050);
    cy.loginAdmin();
  });

  it('passes', () => {
    cy.visit('/');
  });
});
