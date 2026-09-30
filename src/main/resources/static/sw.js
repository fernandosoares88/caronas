// Service Worker Básico
const CACHE_NAME = 'caronas-v1';

// Instala o service worker e faz o cache da página inicial (opcional)
self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(['/']);
    })
  );
});

// Responde às requisições (pode ser configurado para funcionar offline no futuro)
self.addEventListener('fetch', (e) => {
  e.respondWith(
    fetch(e.request).catch(() => caches.match(e.request))
  );
});
