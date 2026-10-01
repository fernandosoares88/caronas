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
self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request).then((response) => {
            return response || fetch(event.request).catch(() => {
                return new Response('Network error occurred', {
                    status: 408,
                    headers: { 'Content-Type': 'text/plain' }
                });
            });
        })
    );
})
