import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth.service';
import { CartService } from '../../core/cart.service';
import { OrderService } from '../../core/order.service';

@Component({
  standalone: true,
  selector: 'app-cart',
  imports: [
    CurrencyPipe,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    MatListModule,
    MatSnackBarModule
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent {
  private readonly orderService = inject(OrderService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly cart = inject(CartService);
  protected readonly submitting = signal(false);

  checkout(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigateByUrl('/login');
      return;
    }

    const items = this.cart.items().map((item) => ({
      productId: item.product.id,
      quantity: item.quantity
    }));

    this.submitting.set(true);
    this.orderService.create({ items }).subscribe({
      next: () => {
        this.cart.clear();
        this.snackBar.open('Order created.', 'Close', { duration: 2500 });
        this.router.navigateByUrl('/orders');
      },
      error: () => {
        this.submitting.set(false);
        this.snackBar.open('Order could not be created.', 'Close', { duration: 3500 });
      }
    });
  }
}
