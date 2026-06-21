document.addEventListener("DOMContentLoaded", () => {
  /*
   * MENÚ RESPONSIVE DE LAS PÁGINAS INTERNAS
   * Mantiene funcionando el botón ☰ de las páginas anteriores.
   */
  const menuToggle = document.getElementById("menuToggle");
  const mainNav = document.getElementById("mainNav");

  if (menuToggle && mainNav) {
    menuToggle.addEventListener("click", () => {
      mainNav.classList.toggle("active");

      const menuIsOpen = mainNav.classList.contains("active");

      menuToggle.setAttribute("aria-expanded", String(menuIsOpen));
      menuToggle.setAttribute(
        "aria-label",
        menuIsOpen ? "Cerrar menú" : "Abrir menú"
      );
    });
  }

  /*
   * MENÚ LATERAL EDITORIAL DE LA PÁGINA PRINCIPAL
   */
  const editorialMenuBtn = document.getElementById("editorialMenuBtn");
  const editorialDrawer = document.getElementById("editorialDrawer");
  const drawerClose = document.getElementById("drawerClose");
  const drawerBackdrop = document.getElementById("drawerBackdrop");

  function openEditorialDrawer() {
    if (!editorialDrawer || !drawerBackdrop) {
      return;
    }

    editorialDrawer.classList.add("active");
    drawerBackdrop.classList.add("active");
    document.body.classList.add("menu-open");

    editorialDrawer.setAttribute("aria-hidden", "false");
    editorialMenuBtn?.setAttribute("aria-expanded", "true");

    drawerClose?.focus();
  }

  function closeEditorialDrawer() {
    if (!editorialDrawer || !drawerBackdrop) {
      return;
    }

    editorialDrawer.classList.remove("active");
    drawerBackdrop.classList.remove("active");
    document.body.classList.remove("menu-open");

    editorialDrawer.setAttribute("aria-hidden", "true");
    editorialMenuBtn?.setAttribute("aria-expanded", "false");

    editorialMenuBtn?.focus();
  }

  editorialMenuBtn?.addEventListener("click", openEditorialDrawer);
  drawerClose?.addEventListener("click", closeEditorialDrawer);
  drawerBackdrop?.addEventListener("click", closeEditorialDrawer);

  /*
   * Cierra el menú lateral cuando se selecciona un enlace.
   */
  const drawerLinks = document.querySelectorAll(".drawer-nav a");

  drawerLinks.forEach((link) => {
    link.addEventListener("click", closeEditorialDrawer);
  });

  /*
   * BUSCADOR SUPERIOR
   */
  const searchButton = document.getElementById("searchButton");
  const searchPanel = document.getElementById("searchPanel");
  const siteSearch = document.getElementById("siteSearch");
  const searchSubmit = document.querySelector(".search-submit");

  function toggleSearchPanel() {
    if (!searchPanel) {
      return;
    }

    searchPanel.classList.toggle("active");

    const searchIsOpen = searchPanel.classList.contains("active");

    searchButton?.setAttribute("aria-expanded", String(searchIsOpen));

    if (searchIsOpen) {
      setTimeout(() => {
        siteSearch?.focus();
      }, 220);
    }
  }

  function closeSearchPanel() {
    if (!searchPanel) {
      return;
    }

    searchPanel.classList.remove("active");
    searchButton?.setAttribute("aria-expanded", "false");
  }

  function performSearch() {
    if (!siteSearch) {
      return;
    }

    const searchTerm = siteSearch.value.trim();

    if (searchTerm === "") {
      siteSearch.focus();
      return;
    }

    /*
     * En esta versión estática todavía no existe un buscador real.
     * Cuando migremos a Spring Boot, esta búsqueda enviará el término
     * hacia una ruta como: /buscar?keyword=texto
     */
    console.log(`Búsqueda solicitada: ${searchTerm}`);

    alert(
      `Buscaste: "${searchTerm}". El buscador se conectará al backend en la versión con Spring Boot.`
    );
  }

  searchButton?.addEventListener("click", toggleSearchPanel);
  searchSubmit?.addEventListener("click", performSearch);

  siteSearch?.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      performSearch();
    }
  });

  /*
   * CERRAR ELEMENTOS CON LA TECLA ESCAPE
   */
  document.addEventListener("keydown", (event) => {
    if (event.key !== "Escape") {
      return;
    }

    if (editorialDrawer?.classList.contains("active")) {
      closeEditorialDrawer();
    }

    if (searchPanel?.classList.contains("active")) {
      closeSearchPanel();
      searchButton?.focus();
    }

    if (mainNav?.classList.contains("active")) {
      mainNav.classList.remove("active");
      menuToggle?.setAttribute("aria-expanded", "false");
      menuToggle?.setAttribute("aria-label", "Abrir menú");
    }
  });

  /*
   * Cierra la navegación móvil cuando se selecciona una sección.
   */
  const mobileNavLinks = document.querySelectorAll(".main-nav a");

  mobileNavLinks.forEach((link) => {
    link.addEventListener("click", () => {
      mainNav?.classList.remove("active");
      menuToggle?.setAttribute("aria-expanded", "false");
      menuToggle?.setAttribute("aria-label", "Abrir menú");
    });
  });

  console.log("Santa Cultura Viva cargado correctamente");
});
