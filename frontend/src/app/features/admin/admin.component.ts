import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Order, OrderStatus, Product, ProductRequest } from '../../core/api.models';
import { OrderService } from '../../core/order.service';
import { ProductService } from '../../core/product.service';

@Component({
  standalone: true,
  selector: 'app-admin',
  imports: [
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly products = signal<Product[]>([]);
  protected readonly orders = signal<Order[]>([]);
  protected readonly editingId = signal<number | null>(null);
  protected readonly productColumns = ['name', 'price', 'stock', 'active', 'actions'];
  protected readonly orderColumns = ['id', 'customer', 'created', 'total', 'status'];
  protected readonly statuses: OrderStatus[] = ['PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    description: ['', [Validators.required]],
    price: [0, [Validators.required, Validators.min(0.01)]],
    stockQuantity: [0, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  ngOnInit(): void {
    this.loadProducts();
    this.loadOrders();
  }

  saveProduct(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: ProductRequest = this.form.getRawValue();
    const editingId = this.editingId();
    const save$ = editingId
      ? this.productService.update(editingId, request)
      : this.productService.create(request);

    save$.subscribe({
      next: () => {
        this.resetForm();
        this.loadProducts();
        this.snackBar.open('Product saved.', 'Close', { duration: 2500 });
      },
      error: () => this.snackBar.open('Product could not be saved.', 'Close', { duration: 3500 })
    });
  }

  editProduct(product: Product): void {
    this.editingId.set(product.id);
    this.form.setValue({
      name: product.name,
      description: product.description,
      price: product.price,
      stockQuantity: product.stockQuantity,
      active: product.active
    });
  }

  archiveProduct(product: Product): void {
    this.productService.delete(product.id).subscribe({
      next: () => {
        this.loadProducts();
        this.snackBar.open('Product archived.', 'Close', { duration: 2500 });
      },
      error: () => this.snackBar.open('Product could not be archived.', 'Close', { duration: 3500 })
    });
  }

  updateStatus(order: Order, status: OrderStatus): void {
    this.orderService.updateStatus(order.id, status).subscribe({
      next: () => {
        this.loadOrders();
        this.snackBar.open('Order status updated.', 'Close', { duration: 2500 });
      },
      error: () => this.snackBar.open('Order status could not be updated.', 'Close', { duration: 3500 })
    });
  }

  resetForm(): void {
    this.editingId.set(null);
    this.form.reset({
      name: '',
      description: '',
      price: 0,
      stockQuantity: 0,
      active: true
    });
  }

  private loadProducts(): void {
    this.productService.list().subscribe((products) => this.products.set(products));
  }

  private loadOrders(): void {
    this.orderService.all().subscribe((orders) => this.orders.set(orders));
  }
}
